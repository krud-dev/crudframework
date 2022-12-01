package dev.krud.crudframework.modelfilter.dsl

import dev.krud.crudframework.modelfilter.FilterField
import dev.krud.crudframework.modelfilter.enums.FilterFieldDataType
import dev.krud.crudframework.modelfilter.enums.FilterFieldOperation

class BetweenBuilder<T>(val fieldName: String, val source: T, val type: FilterFieldDataType) {

    infix fun build(target: T): FilterField {
        return FilterField(
            fieldName,
            FilterFieldOperation.Between,
            type,
            listOf(source, target)
        )
    }
}