package dev.krud.crudframework.crud.handler.krud

import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.dsl.FilterFieldsBuilder
import dev.krud.crudframework.modelfilter.dsl.ModelFilterBuilder
import dev.krud.crudframework.ro.PagedResult
import dev.krud.crudframework.ro.PagedResult.Companion.mapResults
import java.io.Serializable

interface Krud<Entity : BaseCrudEntity<ID>, ID : Serializable> {
    val entityClazz: Class<Entity>

    fun create(entity: Entity, applyPolicies: Boolean = false): Entity

    fun bulkCreate(entities: List<Entity>, applyPolicies: Boolean): List<Entity>

    fun showById(id: ID, cached: Boolean = false, persistCopy: Boolean? = null, applyPolicies: Boolean = false): Entity?

    fun showByFilter(filter: DynamicModelFilter, cached: Boolean = false, persistCopy: Boolean? = null, applyPolicies: Boolean = false): Entity?

    fun showByFilter(cached: Boolean = false, persistCopy: Boolean? = null, applyPolicies: Boolean = false, block: ModelFilterBuilder<Entity>.() -> Unit): Entity? {
        val builder = ModelFilterBuilder<Entity>()
        builder.block()
        val filter = builder.build()
        return showByFilter(filter, cached, persistCopy, applyPolicies)
    }

    fun searchByFilter(filter: DynamicModelFilter, cached: Boolean = false, persistCopy: Boolean? = null, applyPolicies: Boolean = false): PagedResult<Entity>

    fun searchByFilter(cached: Boolean = false, persistCopy: Boolean? = null, applyPolicies: Boolean = false, block: ModelFilterBuilder<Entity>.() -> Unit): PagedResult<Entity> {
        val builder = ModelFilterBuilder<Entity>()
        builder.block()
        val filter = builder.build()
        return searchByFilter(filter, cached, persistCopy, applyPolicies)
    }

    fun searchByFilterCount(filter: DynamicModelFilter, applyPolicies: Boolean = false): Long

    fun searchByFilterCount(applyPolicies: Boolean = false, block: FilterFieldsBuilder<Entity>.() -> Unit): Long {
        val builder = FilterFieldsBuilder<Entity>()
        builder.block()
        val filter = DynamicModelFilter(builder.build().toMutableList())
        return searchByFilterCount(filter, applyPolicies)
    }

    fun update(entity: Entity, applyPolicies: Boolean = false): Entity

    fun updateByFilter(filter: DynamicModelFilter, applyPolicies: Boolean = false, updateBlock: Entity.() -> Unit): PagedResult<Entity> {
        return searchByFilter(filter, applyPolicies = applyPolicies).mapResults {
            it.updateBlock()
            update(it, applyPolicies)
        }
    }

    fun updateByFilter(applyPolicies: Boolean = false, searchBlock: ModelFilterBuilder<Entity>.() -> Unit, updateBlock: Entity.() -> Unit): PagedResult<Entity> {
        val builder = ModelFilterBuilder<Entity>()
        builder.searchBlock()
        val filter = builder.build()
        return updateByFilter(filter, applyPolicies, updateBlock)
    }

    fun updateById(id: ID, applyPolicies: Boolean = false, block: Entity.() -> Unit): Entity {
        val entity = showById(id, applyPolicies = applyPolicies) ?: error("Entity with id $id not found")
        entity.block()
        return update(entity, applyPolicies)
    }

    fun bulkUpdate(entities: List<Entity>, applyPolicies: Boolean): List<Entity>

    fun deleteById(id: ID, applyPolicies: Boolean = false)

    fun delete(entity: Entity, applyPolicies: Boolean = false) = deleteById(entity.id, applyPolicies)

    fun deleteByFilter(filter: DynamicModelFilter, applyPolicies: Boolean = false) {
        searchByFilter(filter, applyPolicies = applyPolicies).forEach {
            delete(it, applyPolicies)
        }
    }

    fun deleteByFilter(applyPolicies: Boolean = false, block: ModelFilterBuilder<Entity>.() -> Unit) {
        val builder = ModelFilterBuilder<Entity>()
        builder.block()
        val filter = builder.build()
        deleteByFilter(filter, applyPolicies)
    }

    fun bulkDelete(ids: List<ID>, applyPolicies: Boolean)
}