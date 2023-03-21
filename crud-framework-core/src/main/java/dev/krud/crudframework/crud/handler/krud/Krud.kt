package dev.krud.crudframework.crud.handler.krud

import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.dsl.FilterFieldsBuilder
import dev.krud.crudframework.modelfilter.dsl.ModelFilterBuilder
import dev.krud.crudframework.ro.PagedResult
import java.io.Serializable

interface Krud<Entity : BaseCrudEntity<ID>, ID : Serializable> {
    val entityClazz: Class<Entity>

    fun create(entity: Entity, applyPolicies: Boolean = false): Entity

    fun bulkCreate(entities: List<Entity>, applyPolicies: Boolean): List<Entity>

    fun showById(id: ID, cached: Boolean = false, persistCopy: Boolean = false, applyPolicies: Boolean = false): Entity?

    fun showByFilter(cached: Boolean = false, persistCopy: Boolean = false, applyPolicies: Boolean = false, block: ModelFilterBuilder<Entity>.() -> Unit): Entity?

    fun showByFilter(filter: DynamicModelFilter, cached: Boolean = false, persistCopy: Boolean = false, applyPolicies: Boolean = false): Entity?

    fun searchByFilter(cached: Boolean = false, persistCopy: Boolean = false, applyPolicies: Boolean = false, block: ModelFilterBuilder<Entity>.() -> Unit): PagedResult<Entity>

    fun searchByFilter(filter: DynamicModelFilter, cached: Boolean = false, persistCopy: Boolean = false, applyPolicies: Boolean = false): PagedResult<Entity>

    fun searchByFilterCount(applyPolicies: Boolean = false, block: FilterFieldsBuilder<Entity>.() -> Unit): Long

    fun searchByFilterCount(filter: DynamicModelFilter, applyPolicies: Boolean = false): Long

    fun update(entity: Entity, applyPolicies: Boolean = false): Entity

    fun updateByFilter(applyPolicies: Boolean = false, searchBlock: ModelFilterBuilder<Entity>.() -> Unit, updateBlock: Entity.() -> Unit) {
        searchByFilter(applyPolicies = applyPolicies, block = searchBlock).forEach {
            it.updateBlock()
            update(it, applyPolicies)
        }
    }

    fun updateById(id: ID, applyPolicies: Boolean = false, block: Entity.() -> Unit) {
        val entity = showById(id, applyPolicies = applyPolicies) ?: error("Entity with id $id not found")
        entity.block()
        update(entity, applyPolicies)
    }

    fun deleteById(id: ID, applyPolicies: Boolean = false)

    fun deleteById(entity: Entity, applyPolicies: Boolean = false) = deleteById(entity.id, applyPolicies)

    fun deleteByFilter(applyPolicies: Boolean = false, block: ModelFilterBuilder<Entity>.() -> Unit) {
        searchByFilter(applyPolicies = applyPolicies, block = block).forEach {
            deleteById(it, applyPolicies)
        }
    }
}