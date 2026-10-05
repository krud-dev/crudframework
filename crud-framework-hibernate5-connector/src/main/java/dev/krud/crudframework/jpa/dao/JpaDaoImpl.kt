package dev.krud.crudframework.jpa.dao

import dev.krud.crudframework.crud.handler.CrudDao
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import java.io.Serializable
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Order
import jakarta.persistence.criteria.Root

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
                cq.roots.first().get<Any>("id")
            )
        )
        return entityManager.createQuery(cq).singleResult as Long
    }

    override fun <ID : Serializable, Entity : BaseCrudEntity<ID>> hardDeleteById(id: ID, clazz: Class<Entity>?) {
        val entity = entityManager.find(clazz, id)
        entityManager.remove(entity)
    }

    override fun <ID : Serializable, Entity : BaseCrudEntity<ID>> saveOrUpdate(entity: Entity): Entity {
        val merged = entityManager.merge(entity)
        entityManager.flush()
        entityManager.refresh(merged)
        return merged
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> saveOrUpdate(entities: MutableList<Entity>): MutableList<Entity> {
        val mergedEntities = entities.map { entityManager.merge(it) }.toMutableList()
        entityManager.flush()
        return mergedEntities.onEach { entityManager.refresh(it) }
    }

    private fun CriteriaBuilder.buildQueryFromFilter(filter: DynamicModelFilter, clazz: Class<*>): CriteriaQuery<*> {
        val cq = createQuery()
        val root = cq.from(clazz)
        val predicates = FilterFieldPredicateBuilder.toPredicates(this, filter.filterFields, root)
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
        root: Root<*>
    ): List<Order> {
        return filter.orders.mapNotNull {
            val by = it.by ?: return@mapNotNull null
            if (it.descending) {
                desc(FilterFieldPredicateBuilder.resolveExpression(root, by))
            } else {
                asc(FilterFieldPredicateBuilder.resolveExpression(root, by))
            }
        }
    }
}
