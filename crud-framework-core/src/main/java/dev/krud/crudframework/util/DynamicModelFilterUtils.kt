@file:JvmName("DynamicModelFilterUtils")

package dev.krud.crudframework.util

import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.FilterField
import dev.krud.crudframework.modelfilter.enums.FilterFieldOperation
import java.lang.reflect.Field

private fun Class<*>.getDeclaredFieldRecursive(name: String): Field {
    var clazz: Class<*>? = this
    while (clazz != null) {
        try {
            return clazz.getDeclaredField(name)
        } catch (e: NoSuchFieldException) {
            clazz = clazz.superclass
        }
    }
    throw NoSuchFieldException(name)
}

private fun getPropertyValue(target: Any, string: String): Any? {
    var item: Any? = target
    val parts = string.split(".").toTypedArray()
    for (part in parts) {
        if (item == null) {
            break
        }
        val clazz: Class<*> = item.javaClass
        val field = clazz.getDeclaredFieldRecursive(part)
        field.isAccessible = true
        item = field.get(item)
    }
    return item
}

/**
 * Check if [target] matches all filter fields in [this]
 */
fun DynamicModelFilter.filtersMatch(target: Any): Boolean {
    return this.filterFields.all { filterField ->
        return@all filterField.filtersMatch(target)
    }
}

/**
 * Check if [target] matches the given filter field
 */
fun FilterField.filtersMatch(target: Any): Boolean {
    if (this.operation == FilterFieldOperation.Noop) {
        return false
    }

    val actualValue: Any? = if (this.operation.junction) {
        null
    } else {
        getPropertyValue(target, this.fieldName.replace("/", "."))
    }
    when (this.operation) {
        FilterFieldOperation.Equal -> {
            return actualValue == this.value1
        }
        FilterFieldOperation.NotEqual -> {
            return actualValue != this.value1
        }
        FilterFieldOperation.In -> {
            return this.value1 in actualValue as Collection<*>
        }
        FilterFieldOperation.NotIn -> {
            return this.value1 !in actualValue as Collection<*>
        }
        FilterFieldOperation.GreaterThan -> {
            actualValue as Comparable<Any>
            val value = this.value1 as Comparable<Any>
            return actualValue > value
        }
        FilterFieldOperation.GreaterEqual -> {
            actualValue as Comparable<Any>
            val value = this.value1 as Comparable<Any>
            return actualValue >= value
        }
        FilterFieldOperation.LowerThan -> {
            actualValue as Comparable<Any>
            val value = this.value1 as Comparable<Any>
            return actualValue < value
        }
        FilterFieldOperation.LowerEqual -> {
            actualValue as Comparable<Any>
            val value = this.value1 as Comparable<Any>
            return actualValue <= value
        }
        FilterFieldOperation.Between -> {
            actualValue as Comparable<Any>
            val value1 = this.value1 as Comparable<Any>
            val value2 = this.value2 as Comparable<Any>
            return actualValue >= value1 && actualValue < value2
        }
        FilterFieldOperation.Contains -> {
            actualValue as String
            val value = this.value1 as String
            return actualValue.contains(value)
        }
        FilterFieldOperation.IsNull -> {
            return actualValue == null
        }
        FilterFieldOperation.IsNotNull -> {
            return actualValue != null
        }
        FilterFieldOperation.IsEmpty -> {
            val value = this.value1 as Collection<*>
            return value.isEmpty()
        }
        FilterFieldOperation.IsNotEmpty -> {
            val value = this.value1 as Collection<*>
            return value.isNotEmpty()
        }
        FilterFieldOperation.And -> {
            return children.all { it.filtersMatch(target) }
        }
        FilterFieldOperation.Or -> {
            return children.any { it.filtersMatch(target) }
        }
        FilterFieldOperation.Not -> {
            return children.none { it.filtersMatch(target) }
        }
        FilterFieldOperation.Noop -> return false
        null -> return false
    }
}