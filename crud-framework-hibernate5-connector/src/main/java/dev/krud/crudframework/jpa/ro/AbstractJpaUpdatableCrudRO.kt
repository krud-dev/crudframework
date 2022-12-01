package dev.krud.crudframework.jpa.ro

import java.io.Serializable
import java.util.Date

abstract class AbstractJpaUpdatableCrudRO : Serializable {
    val id: Long? = null
    val creationTime: Date? = null
    val lastUpdateTime: Date? = null
}