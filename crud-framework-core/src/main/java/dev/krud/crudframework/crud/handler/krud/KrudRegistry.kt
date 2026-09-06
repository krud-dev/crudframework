package dev.krud.crudframework.crud.handler.krud

import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

/**
 * Holds a direct entityClass -> Krud mapping, populated by each [KrudImpl] as it finishes
 * initialization. Lookups here are O(1) map reads, unlike resolving a Krud bean through the
 * application context by its parameterized type, which requires a generic-aware scan of every
 * bean definition.
 */
class KrudRegistry {
    private val kruds = ConcurrentHashMap<Class<*>, Krud<*, *>>()

    fun register(entityClazz: Class<*>, krud: Krud<*, *>) {
        kruds[entityClazz] = krud
    }

    @Suppress("UNCHECKED_CAST")
    fun <Entity : Any, ID : Serializable> getOrNull(entityClazz: Class<Entity>): Krud<Entity, ID>? =
        kruds[entityClazz] as Krud<Entity, ID>?
}
