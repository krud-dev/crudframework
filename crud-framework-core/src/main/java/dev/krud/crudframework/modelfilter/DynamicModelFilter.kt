package dev.krud.crudframework.modelfilter

class DynamicModelFilter(
        var start: Long? = null,
        var limit: Long? = null,
        var orders: MutableList<OrderDTO> = mutableListOf(),
        val filterFields: MutableList<FilterField> = mutableListOf()
) {
    val cacheKey: String get() = "CacheKey_" + this.javaClass.simpleName + "_" + this.hashCode()

    constructor() : this(null, null, mutableListOf(), mutableListOf())

    constructor(filterFields: MutableList<FilterField>) : this(null, null, mutableListOf(), filterFields)

    fun add(filterField: FilterField): DynamicModelFilter {
        filterFields.add(filterField)
        return this
    }

    fun addOrder(orderDTO: OrderDTO): DynamicModelFilter {
        orders.add(orderDTO)
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DynamicModelFilter

        if (start != other.start) return false
        if (limit != other.limit) return false
        if (orders != other.orders) return false
        if (filterFields != other.filterFields) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start?.hashCode() ?: 0
        result = 31 * result + (limit?.hashCode() ?: 0)
        result = 31 * result + orders.hashCode()
        result = 31 * result + filterFields.hashCode()
        return result
    }
}