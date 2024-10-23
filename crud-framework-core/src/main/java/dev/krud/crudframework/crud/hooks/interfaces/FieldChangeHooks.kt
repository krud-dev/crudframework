package dev.krud.crudframework.crud.hooks.interfaces

import dev.krud.crudframework.model.BaseCrudEntity
import java.io.Serializable

interface FieldChangeHooks<ID : Serializable, Entity : BaseCrudEntity<ID>> : CRUDHooks<ID, Entity> {
    fun registeredFieldChangeHooks(): List<FieldChangeHook<*, *>>
}