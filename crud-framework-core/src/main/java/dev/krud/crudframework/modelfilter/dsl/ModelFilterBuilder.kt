package dev.krud.crudframework.modelfilter.dsl

import dev.krud.crudframework.model.PersistentEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.FilterField
import dev.krud.crudframework.modelfilter.OrderDTO
import dev.krud.crudframework.modelfilter.dsl.annotation.FilterFieldDsl

@FilterFieldDsl
class ModelFilterBuilder<RootType : PersistentEntity>(
    var orders: MutableSet<OrderDTO> = mutableSetOf(),
    var start: Int = 0,
    var limit: Int = 10000,
    var filterFields: MutableList<FilterField> = mutableListOf()
) {

    fun where(setup: FilterFieldsBuilder<RootType>.() -> Unit) {
        val filterFieldsBuilder = FilterFieldsBuilder<RootType>()
        filterFieldsBuilder.setup()
        this.filterFields.addAll(filterFieldsBuilder.build())
    }

    fun order(setup: OrderBuilder<RootType>.() -> Unit) {
        val orderBuilder = OrderBuilder<RootType>()
        orderBuilder.setup()
        val dto = orderBuilder.build()
        this.orders.add(dto)
    }

    fun build(): DynamicModelFilter {
        return DynamicModelFilter(
            start,
            limit,
            orders,
            filterFields
        )
    }
}