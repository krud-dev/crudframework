package dev.krud.crudframework.jpa.dao

import dev.krud.crudframework.modelfilter.FilterField
import dev.krud.crudframework.modelfilter.enums.FilterFieldOperation
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.metamodel.Attribute
import jakarta.persistence.metamodel.PluralAttribute

/**
 * Turns [FilterField]s into JPA [Predicate]s against any [From]: a query root, a join, or a subquery root.
 *
 * [JpaDaoImpl] builds every index/count query through this, so callers that build their own criteria queries
 * (for example applying an entity's effective read filter to a joined alias or a subquery) get exactly the same
 * filter semantics as a regular read.
 */
object FilterFieldPredicateBuilder {
    fun toPredicates(cb: CriteriaBuilder, filterFields: Collection<FilterField>, from: From<*, *>): List<Predicate> {
        return filterFields.map { toPredicate(cb, it, from) }
    }

    fun toPredicate(cb: CriteriaBuilder, filterField: FilterField, from: From<*, *>): Predicate = with(cb) {
        when (filterField.operation) {
            FilterFieldOperation.Equal -> {
                equal(resolveExpression(from, filterField.fieldName), filterField.value1())
            }

            FilterFieldOperation.EqualIgnoreCase -> {
                val path = resolveExpression(from, filterField.fieldName) as Path<String>
                equal(
                    lower(path),
                    filterField.value1()?.toString()?.lowercase()
                )
            }

            FilterFieldOperation.NotEqual -> {
                notEqual(resolveExpression(from, filterField.fieldName), filterField.value1())
            }

            FilterFieldOperation.NotEqualIgnoreCase -> {
                val path = resolveExpression(from, filterField.fieldName) as Path<String>
                notEqual(
                    lower(path),
                    filterField.value1()?.toString()?.lowercase()
                )
            }

            FilterFieldOperation.In -> {
                `in`(resolveExpression(from, filterField.fieldName)).value(filterField.values.toList())
            }

            FilterFieldOperation.NotIn -> {
                not(`in`(resolveExpression(from, filterField.fieldName)).value(filterField.values.toList()))
            }

            FilterFieldOperation.GreaterThan -> {
                greaterThan(
                    resolveExpression(from, filterField.fieldName) as Expression<out Comparable<Any>>,
                    filterField.value1() as Comparable<Any>
                )
            }

            FilterFieldOperation.GreaterEqual -> {
                greaterThanOrEqualTo(
                    resolveExpression(from, filterField.fieldName) as Expression<out Comparable<Any>>,
                    filterField.value1() as Comparable<Any>
                )
            }

            FilterFieldOperation.LowerThan -> {
                lessThan(
                    resolveExpression(from, filterField.fieldName) as Expression<out Comparable<Any>>,
                    filterField.value1() as Comparable<Any>
                )
            }

            FilterFieldOperation.LowerEqual -> {
                lessThanOrEqualTo(
                    resolveExpression(from, filterField.fieldName) as Expression<out Comparable<Any>>,
                    filterField.value1() as Comparable<Any>
                )
            }

            FilterFieldOperation.Between -> {
                between(
                    resolveExpression(from, filterField.fieldName) as Expression<out Comparable<Any>>,
                    filterField.value1() as Comparable<Any>,
                    filterField.value2() as Comparable<Any>
                )
            }

            FilterFieldOperation.Contains -> {
                val path = resolveExpression(from, filterField.fieldName)
                val attribute = (path as? Path<*>)?.model
                if (attribute is PluralAttribute<*, *, *> && attribute.persistentAttributeType == Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
                    isMember(
                        castToElementType(filterField.value1(), attribute.elementType.javaType),
                        path as Expression<Collection<Any?>>
                    )
                } else if (Collection::class.java.isAssignableFrom(path.javaType)) {
                    equal(
                        function(
                            "JSON_CONTAINS",
                            Integer::class.java,
                            path,
                            function("JSON_QUOTE", String::class.java, literal(filterField.value1().toString()))
                        ),
                        1
                    )
                } else {
                    like(path as Expression<String>, literal("%${filterField.value1()}%"))
                }
            }

            FilterFieldOperation.IsNull -> {
                isNull(resolveExpression(from, filterField.fieldName))
            }

            FilterFieldOperation.IsNotNull -> {
                isNotNull(resolveExpression(from, filterField.fieldName))
            }

            FilterFieldOperation.IsEmpty -> {
                this.isEmpty(resolveExpression(from, filterField.fieldName) as Path<Collection<*>>)
            }

            FilterFieldOperation.IsNotEmpty -> {
                this.isNotEmpty(resolveExpression(from, filterField.fieldName) as Path<Collection<*>>)
            }

            FilterFieldOperation.And -> {
                and(*filterField.children.map { toPredicate(cb, it, from) }.toTypedArray())
            }

            FilterFieldOperation.Or -> {
                or(*filterField.children.map { toPredicate(cb, it, from) }.toTypedArray())
            }

            FilterFieldOperation.Not -> {
                not(toPredicate(cb, filterField.children.first(), from))
            }

            FilterFieldOperation.Noop -> {
                equal(literal(true), literal(false))
            }

            else -> error("Unknown operation: ${filterField.operation}")
        }
    }

    /**
     * Resolves a field name, possibly a nested path such as `business.brand.id`, against [from].
     * Intermediate associations are inner joined on [from], reusing a join that already exists for the same attribute.
     */
    fun resolveExpression(from: From<*, *>, fieldName: String): Expression<*> {
        if (!fieldName.contains(".")) {
            return from.get<Any>(fieldName)
        }

        var expression = from
        val parts = fieldName.replace("/", ".").split(".")
        for (i in 0..parts.size - 2) {
            expression = expression.joins.find { it.attribute.name == parts[i] } ?: expression.join<Any, Any>(parts[i])
        }

        return expression.get<Any>(parts[parts.size - 1])
    }

    private fun castToElementType(value: Any?, elementType: Class<*>): Any? {
        if (value == null || elementType.isInstance(value) || !elementType.isEnum) {
            return value
        }

        return elementType.enumConstants.first { (it as Enum<*>).name == value.toString() }
    }
}
