package dev.krud.crudframework.crud.hooks.interfaces

import dev.krud.crudframework.model.BaseCrudEntity
import kotlin.reflect.KProperty1

inline fun <reified T, reified E : BaseCrudEntity<*>> KProperty1<E, T>.preChange(
    crossinline block: (T?, T?, E) -> Unit
): FieldChangeHook<T, E> = fieldChangeHook(this, preChange = block)

inline fun <reified T, reified E : BaseCrudEntity<*>> KProperty1<E, T>.onChange(
    crossinline block: (T?, T?, E) -> Unit
): FieldChangeHook<T, E> = fieldChangeHook(this, onChange = block)

inline fun <reified T, reified E : BaseCrudEntity<*>> KProperty1<E, T>.postChange(
    crossinline block: (T?, T?, E) -> Unit
): FieldChangeHook<T, E> = fieldChangeHook(this, postChange = block)

inline fun <reified E : BaseCrudEntity<*>> Collection<KProperty1<E, *>>.preChange(
    checkType: MultiFieldChangeCheckType = MultiFieldChangeCheckType.ANY,
    crossinline block: (E, E) -> Unit
): MultiFieldChangeHook<E> = multiFieldChangeHook(this.toList(), checkType, preChange = block)

inline fun <reified E : BaseCrudEntity<*>> Collection<KProperty1<E, *>>.onChange(
    checkType: MultiFieldChangeCheckType = MultiFieldChangeCheckType.ANY,
    crossinline block: (E, E) -> Unit
): MultiFieldChangeHook<E> = multiFieldChangeHook(this.toList(), checkType, onChange = block)

inline fun <reified E : BaseCrudEntity<*>> Collection<KProperty1<E, *>>.postChange(
    checkType: MultiFieldChangeCheckType = MultiFieldChangeCheckType.ANY,
    crossinline block: (E, E) -> Unit
): MultiFieldChangeHook<E> = multiFieldChangeHook(this.toList(), checkType, postChange = block)
