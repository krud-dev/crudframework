package dev.krud.crudframework.jpa.dao

import dev.krud.crudframework.crud.handler.CrudDao
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.FilterField
import dev.krud.crudframework.modelfilter.enums.FilterFieldOperation
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.*
import java.io.Serializable

class JpaDaoImpl : CrudDao {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun <ID : Serializable, Entity : BaseCrudEntity<ID>, E : DynamicModelFilter> index(
        filter: E,
        clazz: Class<Entity>
    ): MutableList<Entity> {
        val cb = entityManager.criteriaBuilder
        val cq = cb.buildQueryFromFilter(filter, clazz)
        val query = entityManager.createQuery(cq)
        setLimits(filter, query)
        return query.resultList as MutableList<Entity>
    }

    override fun <ID : Serializable, Entity : BaseCrudEntity<ID>, E : DynamicModelFilter> indexCount(
        filter: E,
        clazz: Class<Entity>
    ): Long {
        val cb = entityManager.criteriaBuilder
        val cq = cb.buildQueryFromFilter(filter, clazz) as CriteriaQuery<Comparable<*>>
        cq.select(
            cb.count(
                cq.from(clazz).get<Any>("id")
            )
        )
        return entityManager.createQuery(cq).singleResult as Long
    }

    override fun <ID : Serializable, Entity : BaseCrudEntity<ID>> hardDeleteById(id: ID, clazz: Class<Entity>?) {
        val entity = entityManager.find(clazz, id)
        entityManager.remove(entity)
    }

    override fun <ID : Serializable, Entity : BaseCrudEntity<ID>> saveOrUpdate(entity: Entity): Entity {
        return entityManager.merge(entity)
    }

    private fun CriteriaBuilder.buildQueryFromFilter(filter: DynamicModelFilter, clazz: Class<*>): CriteriaQuery<*> {
        val cq = createQuery()
        val root = cq.from(clazz)
        val predicates = filter.filterFields
            .map { processFilterField(it, root) }
            .toTypedArray()
        if (predicates.isNotEmpty()) {
            cq.where(*predicates)
        }
        if (filter.orders.isNotEmpty()) {
            cq.orderBy(getOrders(filter, root))
        }
        return cq
    }

    private fun setLimits(filter: DynamicModelFilter, query: TypedQuery<*>) {
        filter.start?.let {
            query.firstResult = it.toInt()
        }

        filter.limit?.let {
            query.maxResults = it.toInt()
        }
    }

    private fun CriteriaBuilder.getOrders(
        filter: DynamicModelFilter,
        root: Root<*>,
    ): List<Order> {
        return filter.orders.mapNotNull {
            val by = it.by ?: return@mapNotNull null
            if (it.descending) {
                desc(root.getExpressionByFieldName(by))
            } else {
                asc(root.getExpressionByFieldName(by))
            }
        }
    }

    private fun CriteriaBuilder.processFilterField(filterField: FilterField, root: Root<*>): Predicate {
        val predicate: Predicate = when (filterField.operation) {
            FilterFieldOperation.Equal -> {
                equal(root.getExpressionByFieldName(filterField.fieldName), filterField.value1())
            }

            FilterFieldOperation.NotEqual -> {
                notEqual(root.getExpressionByFieldName(filterField.fieldName), filterField.value1())
            }

            FilterFieldOperation.In -> {
                `in`(root.getExpressionByFieldName(filterField.fieldName)).value(filterField.value1())
            }

            FilterFieldOperation.NotIn -> {
                not(`in`(root.getExpressionByFieldName(filterField.fieldName)).value(filterField.value1()))
            }

            FilterFieldOperation.GreaterThan -> {
                greaterThan(
                    root.getExpressionByFieldName(filterField.fieldName) as Expression<out Comparable<Any>>,
                    filterField.value1() as Comparable<Any>
                );
            }

            FilterFieldOperation.GreaterEqual -> {
                greaterThanOrEqualTo(
                    root.getExpressionByFieldName(filterField.fieldName) as Expression<out Comparable<Any>>,
                    filterField.value1() as Comparable<Any>
                )
            }

            FilterFieldOperation.LowerThan -> {
                lessThan(
                    root.getExpressionByFieldName(filterField.fieldName) as Expression<out Comparable<Any>>,
                    filterField.value1() as Comparable<Any>
                )
            }

            FilterFieldOperation.LowerEqual -> {
                lessThanOrEqualTo(
                    root.getExpressionByFieldName(filterField.fieldName) as Expression<out Comparable<Any>>,
                    filterField.value1() as Comparable<Any>
                )
            }

            FilterFieldOperation.Between -> {
                between(
                    root.getExpressionByFieldName(filterField.fieldName) as Expression<out Comparable<Any>>,
                    filterField.value1() as Comparable<Any>,
                    filterField.value2() as Comparable<Any>
                )
            }

            FilterFieldOperation.Contains -> {
                like(root.getExpressionByFieldName(filterField.fieldName) as Path<String>, "%${filterField.value1()}%")
            }

            FilterFieldOperation.IsNull -> {
                isNull(root.getExpressionByFieldName(filterField.fieldName))
            }

            FilterFieldOperation.IsNotNull -> {
                isNotNull(root.getExpressionByFieldName(filterField.fieldName))
            }

            FilterFieldOperation.IsEmpty -> {
                this.isEmpty(root.getExpressionByFieldName(filterField.fieldName) as Path<Collection<*>>)
            }

            FilterFieldOperation.IsNotEmpty -> {
                this.isNotEmpty(root.getExpressionByFieldName(filterField.fieldName) as Path<Collection<*>>)
            }

            FilterFieldOperation.And -> {
                and(*filterField.children.map { processFilterField(it, root) }.toTypedArray())
            }

            FilterFieldOperation.Or -> {
                or(*filterField.children.map { processFilterField(it, root) }.toTypedArray())
            }

            FilterFieldOperation.Not -> {
                not(processFilterField(filterField.children.first(), root))
            }

            FilterFieldOperation.Noop -> {
                equal(literal(true), literal(false))
            }

            else -> error("Unknown operation: ${filterField.operation}")
        }

        return predicate
    }

    private fun From<*, *>.getExpressionByFieldName(fieldName: String): Expression<*> {
        if (!fieldName.contains(".")) {
            return this.get<Any>(fieldName)
        }
        // Split first name into first node, and rest
        val firstNode = fieldName.substringBefore(".")

        return this.join<Any, Any>(firstNode).getExpressionByFieldName(fieldName.substringAfter("."))
    }
}