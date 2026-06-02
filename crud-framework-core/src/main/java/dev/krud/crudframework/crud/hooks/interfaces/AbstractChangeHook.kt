package dev.krud.crudframework.crud.hooks.interfaces

import dev.krud.crudframework.model.BaseCrudEntity

abstract class AbstractChangeHook<Entity : BaseCrudEntity<*>> {
    abstract fun runPreChange(entity: Entity, original: Entity)
    abstract fun runOnChange(entity: Entity, original: Entity)
    abstract fun runPostChange(entity: Entity, original: Entity)
}
