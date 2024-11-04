package dev.krud.crudframework.crud.hooks.interfaces

import dev.krud.crudframework.model.BaseCrudEntity
import kotlin.reflect.KProperty1

abstract class FieldChangeHook<PropertyType, Entity : BaseCrudEntity<*>>(val property: KProperty1<Entity, PropertyType>) {
    open fun preChange(originalValue: PropertyType?, newValue: PropertyType?, entity: Entity) {}
    open fun onChange(originalValue: PropertyType?, newValue: PropertyType?, entity: Entity) {}
    open fun postChange(originalValue: PropertyType?, newValue: PropertyType?, entity: Entity) {}

    fun shouldTrigger(entity: Entity, original: Entity): Boolean {
        val originalValue = property.get(original) as PropertyType?
        val newValue = property.get(entity) as PropertyType?
        return originalValue != newValue
    }

    fun runPreChange(entity: Entity, original: Entity) {
        val originalValue = property.get(original) as PropertyType?
        val newValue = property.get(entity) as PropertyType?
        if(originalValue == newValue) return
        preChange(originalValue, newValue, entity)
    }

    fun runOnChange(entity: Entity, original: Entity) {
        val originalValue = property.get(original) as PropertyType?
        val newValue = property.get(entity) as PropertyType?
        if(originalValue == newValue) return
        onChange(originalValue, newValue, entity)
    }

    fun runPostChange(entity: Entity, original: Entity) {
        val originalValue = property.get(original) as PropertyType?
        val newValue = property.get(entity) as PropertyType?
        if(originalValue == newValue) return
        postChange(originalValue, newValue, entity)
    }

}

inline fun <reified T, reified E : BaseCrudEntity<*>> fieldChangeHook(
    property: KProperty1<E, T>,
    crossinline preChange: (T?, T?, E) -> Unit = { _, _, _ -> },
    crossinline onChange: (T?, T?, E) -> Unit = { _, _, _ -> },
    crossinline postChange: (T?, T?, E) -> Unit = { _, _, _ -> },
): FieldChangeHook<T, E> {
    return object : FieldChangeHook<T, E>(property) {
        override fun preChange(originalValue: T?, newValue: T?, entity: E) = preChange(originalValue, newValue, entity)
        override fun onChange(originalValue: T?, newValue: T?, entity: E) = onChange(originalValue, newValue, entity)
        override fun postChange(originalValue: T?, newValue: T?, entity: E) = postChange(originalValue, newValue, entity)
    }
}