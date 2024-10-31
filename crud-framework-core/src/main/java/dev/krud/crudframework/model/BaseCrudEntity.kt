package dev.krud.crudframework.model

import org.springframework.beans.BeanUtils
import java.io.Serializable

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
                        BeanUtils.copyProperties(this, internalCopy)
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