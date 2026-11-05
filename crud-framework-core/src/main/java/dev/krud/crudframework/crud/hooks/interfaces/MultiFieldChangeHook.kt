package dev.krud.crudframework.crud.hooks.interfaces

import dev.krud.crudframework.model.BaseCrudEntity
import kotlin.reflect.KProperty1

enum class MultiFieldChangeCheckType {
    ALL,
    ANY
}

/**
 * Runs when any (or all, see [checkType]) of [properties] changed on update.
 *
 * Each callback gets its own detached copy of the original entity, not the shared copy every change hook
 * of the update compares against, so changing it inside a callback can't affect other hooks. `updated` is
 * the entity being saved. A copy rather than a proxy, since a class proxy can't wrap a final Kotlin class
 * and doesn't reach the values behind its final properties.
 */
abstract class MultiFieldChangeHook<Entity : BaseCrudEntity<*>>(
    val properties: List<KProperty1<Entity, *>>,
    val checkType: MultiFieldChangeCheckType = MultiFieldChangeCheckType.ANY
) : AbstractChangeHook<Entity>() {
    open fun preChange(original: Entity, updated: Entity) {}
    open fun onChange(original: Entity, updated: Entity) {}
    open fun postChange(original: Entity, updated: Entity) {}

    fun shouldTrigger(entity: Entity, original: Entity): Boolean {
        return when (checkType) {
            MultiFieldChangeCheckType.ALL -> properties.all { it.getValue(original) != it.getValue(entity) }
            MultiFieldChangeCheckType.ANY -> properties.any { it.getValue(original) != it.getValue(entity) }
        }
    }

    override fun runPreChange(entity: Entity, original: Entity) {
        if (!shouldTrigger(entity, original)) return
        preChange(detachedCopy(original), entity)
    }

    override fun runOnChange(entity: Entity, original: Entity) {
        if (!shouldTrigger(entity, original)) return
        onChange(detachedCopy(original), entity)
    }

    override fun runPostChange(entity: Entity, original: Entity) {
        if (!shouldTrigger(entity, original)) return
        postChange(detachedCopy(original), entity)
    }

    @Suppress("UNCHECKED_CAST")
    private fun detachedCopy(original: Entity): Entity = original.detachedCopy() as Entity

    private fun <E, T> KProperty1<E, T?>.getValue(instance: E): T? {
        return try {
            this.get(instance)
        } catch (e: UninitializedPropertyAccessException) {
            null
        }
    }
}

inline fun <reified E : BaseCrudEntity<*>> multiFieldChangeHook(
    properties: List<KProperty1<E, *>>,
    checkType: MultiFieldChangeCheckType = MultiFieldChangeCheckType.ANY,
    crossinline preChange: (E, E) -> Unit = { _, _ -> },
    crossinline onChange: (E, E) -> Unit = { _, _ -> },
    crossinline postChange: (E, E) -> Unit = { _, _ -> },
): MultiFieldChangeHook<E> {
    return object : MultiFieldChangeHook<E>(properties, checkType) {
        override fun preChange(original: E, updated: E) = preChange(original, updated)
        override fun onChange(original: E, updated: E) = onChange(original, updated)
        override fun postChange(original: E, updated: E) = postChange(original, updated)
    }
}
