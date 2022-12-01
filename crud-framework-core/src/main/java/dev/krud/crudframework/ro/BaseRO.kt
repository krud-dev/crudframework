package dev.krud.crudframework.ro

import java.io.Serializable
import java.util.*

/**
 * Date: 10.01.13 Time: 20:27
 *
 * @author Shani Holdengreber
 * @author thewizkid@gmail.com
 */
abstract class BaseRO<ID> : Serializable {
    abstract var id: ID?

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseRO<*>

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}