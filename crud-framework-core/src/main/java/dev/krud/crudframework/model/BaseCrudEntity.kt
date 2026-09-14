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
     * Like [BeanUtils.copyProperties], but copies property-by-property so that an uninitialized
     * Kotlin `lateinit var` on [source] (which throws [UninitializedPropertyAccessException] when read)
     * only skips that one property on [target] instead of aborting the entire copy.
     */
    private fun copyPropertiesIgnoringUninitializedLateinit(source: Any, target: Any) {
        for (descriptor in BeanUtils.getPropertyDescriptors(source.javaClass)) {
            val readMethod = descriptor.readMethod ?: continue
            val writeMethod = descriptor.writeMethod ?: continue
            try {
                writeMethod.invoke(target, readMethod.invoke(source))
            } catch (e: InvocationTargetException) {
                if (e.targetException !is UninitializedPropertyAccessException) {
                    throw e
                }
                try {
                    writeMethod.invoke(target, null)
                } catch (ignored: IllegalArgumentException) {
                    // primitive property (e.g. Int/Boolean) - leave the no-arg-constructor default
                }
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