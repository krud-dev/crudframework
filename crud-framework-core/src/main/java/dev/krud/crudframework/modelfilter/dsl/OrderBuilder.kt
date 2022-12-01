package dev.krud.crudframework.modelfilter.dsl

import dev.krud.crudframework.model.PersistentEntity
import dev.krud.crudframework.modelfilter.OrderDTO
import dev.krud.crudframework.modelfilter.dsl.annotation.FilterFieldDsl
import kotlin.reflect.KProperty1

@FilterFieldDsl
class OrderBuilder<RootType : PersistentEntity>(var by: KProperty1<RootType, *>? = null, private var desc: Boolean = true) {
    val descending: Unit
        get() {
            desc = true
        }
    val ascending: Unit
        get() {
            desc = false
        }

    fun build() = OrderDTO(by?.name ?: "id", desc)
}