package dev.krud.crudframework.crud.hooks.interfaces

import dev.krud.crudframework.model.BaseCrudEntity
import org.aopalliance.intercept.MethodInterceptor
import org.springframework.aop.framework.ProxyFactory
import kotlin.reflect.KProperty1

enum class MultiFieldChangeCheckType {
    ALL,
    ANY
}

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
        preChange(makeImmutable(original), entity)
    }

    override fun runOnChange(entity: Entity, original: Entity) {
        if (!shouldTrigger(entity, original)) return
        onChange(makeImmutable(original), entity)
    }

    override fun runPostChange(entity: Entity, original: Entity) {
        if (!shouldTrigger(entity, original)) return
        postChange(makeImmutable(original), entity)
    }

    @Suppress("UNCHECKED_CAST")
    private fun makeImmutable(entity: Entity): Entity {
        val proxyFactory = ProxyFactory(entity)
        proxyFactory.addAdvice(MethodInterceptor { invocation ->
            if (invocation.method.name.startsWith("set")) {
                throw UnsupportedOperationException("Cannot modify the original entity inside a MultiFieldChangeHook callback")
            }
            invocation.proceed()
        })
        return proxyFactory.proxy as Entity
    }

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
