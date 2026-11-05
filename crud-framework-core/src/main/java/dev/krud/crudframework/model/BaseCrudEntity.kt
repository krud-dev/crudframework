package dev.krud.crudframework.model

import org.springframework.beans.BeanUtils
import java.io.Serializable
import java.lang.reflect.InvocationTargetException

abstract class BaseCrudEntity<ID : Serializable> : PersistentEntity, Serializable {

    abstract var id: ID

    @Transient
    private var copy: BaseCrudEntity<ID>? = null

    @Transient
    private var isCopy: Boolean = false

    fun saveOrGetCopy(): BaseCrudEntity<ID>? {
        if (copy == null) {
            try {
                if (!isCopy) {
                    val internalCopy = javaClass.newInstance()
                    internalCopy.isCopy = true
                    if(id != internalCopy.id) {
                        copyPropertiesIgnoringUninitializedLateinit(this, internalCopy)
                    }
                    copy = internalCopy // ImmutableBean.create(internalCopy) as BaseCrudEntity<ID>
                } else {
                    return null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return copy
    }

    /**
     * A new shallow copy of this entity's current property values. Unlike [saveOrGetCopy] it is never cached,
     * so changes made to it reach neither this entity nor its saved copy. It needs a no-arg constructor,
     * like [saveOrGetCopy].
     */
    internal fun detachedCopy(): BaseCrudEntity<ID> {
        val detached = javaClass.getDeclaredConstructor().newInstance()
        detached.isCopy = true
        copyPropertiesIgnoringUninitializedLateinit(this, detached)
        return detached
    }

    /**
     * Like [BeanUtils.copyProperties], but copies property-by-property so that one property that can't be copied
     * only skips that property on [target] instead of aborting the entire copy:
     * - an uninitialized Kotlin `lateinit var` on [source] (throws [UninitializedPropertyAccessException] when read)
     *   is copied as null
     * - a null the setter rejects keeps [target]'s no-arg-constructor default: a primitive property, a `lateinit var`,
     *   or a non-null Kotlin property left null by a JPA no-arg constructor (e.g. on [generateEmptyEntity])
     */
    private fun copyPropertiesIgnoringUninitializedLateinit(source: Any, target: Any) {
        for (descriptor in BeanUtils.getPropertyDescriptors(source.javaClass)) {
            val readMethod = descriptor.readMethod ?: continue
            val writeMethod = descriptor.writeMethod ?: continue
            val value = try {
                readMethod.invoke(source)
            } catch (e: InvocationTargetException) {
                if (e.targetException !is UninitializedPropertyAccessException) {
                    throw e
                }
                null
            }
            try {
                writeMethod.invoke(target, value)
            } catch (e: IllegalArgumentException) {
                if (value != null) throw e
            } catch (e: InvocationTargetException) {
                if (value != null) throw e
            }
        }
    }

    fun generateEmptyEntity(): BaseCrudEntity<ID>? {
        try {
            val emptyEntity = javaClass.newInstance()
            emptyEntity.isCopy = true
            return emptyEntity // ImmutableBean.create(internalCopy) as BaseCrudEntity<ID>
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    open fun getCacheKey(): String? {
        return getCacheKey(javaClass, id)
    }

    abstract fun exists(): Boolean

    companion object {
        fun getCacheKey(clazz: Class<*>, id: Serializable?): String? {
            return "CacheKey_" + clazz.simpleName + "_" + id
        }
    }
}