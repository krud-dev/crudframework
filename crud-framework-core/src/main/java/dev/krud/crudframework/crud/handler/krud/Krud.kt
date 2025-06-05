package dev.krud.crudframework.crud.handler.krud

import dev.krud.crudframework.crud.hooks.HooksDTO
import dev.krud.crudframework.crud.hooks.create.CRUDOnCreateHook
import dev.krud.crudframework.crud.hooks.create.CRUDPostCreateHook
import dev.krud.crudframework.crud.hooks.create.CRUDPreCreateHook
import dev.krud.crudframework.crud.hooks.delete.CRUDOnDeleteHook
import dev.krud.crudframework.crud.hooks.delete.CRUDPostDeleteHook
import dev.krud.crudframework.crud.hooks.delete.CRUDPreDeleteHook
import dev.krud.crudframework.crud.hooks.index.CRUDOnIndexHook
import dev.krud.crudframework.crud.hooks.index.CRUDPostIndexHook
import dev.krud.crudframework.crud.hooks.index.CRUDPreIndexHook
import dev.krud.crudframework.crud.hooks.show.CRUDOnShowHook
import dev.krud.crudframework.crud.hooks.show.CRUDPostShowHook
import dev.krud.crudframework.crud.hooks.show.CRUDPreShowHook
import dev.krud.crudframework.crud.hooks.show.by.CRUDOnShowByHook
import dev.krud.crudframework.crud.hooks.show.by.CRUDPostShowByHook
import dev.krud.crudframework.crud.hooks.show.by.CRUDPreShowByHook
import dev.krud.crudframework.crud.hooks.update.CRUDOnUpdateHook
import dev.krud.crudframework.crud.hooks.update.CRUDPostUpdateHook
import dev.krud.crudframework.crud.hooks.update.CRUDPreUpdateHook
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.dsl.FilterFieldsBuilder
import dev.krud.crudframework.modelfilter.dsl.ModelFilterBuilder
import dev.krud.crudframework.ro.PagedResult
import dev.krud.crudframework.ro.PagedResult.Companion.mapResults
import java.io.Serializable

interface Krud<Entity : BaseCrudEntity<ID>, ID : Serializable> {
    val entityClazz: Class<Entity>

    fun create(entity: Entity, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreCreateHook<ID, Entity>, CRUDOnCreateHook<ID, Entity>, CRUDPostCreateHook<ID, Entity>> = noHooks()): Entity

    fun bulkCreate(entities: List<Entity>, applyPolicies: Boolean): List<Entity>

    fun showById(id: ID, cached: Boolean = false, persistCopy: Boolean? = null, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreShowHook<ID, Entity>, CRUDOnShowHook<ID, Entity>, CRUDPostShowHook<ID, Entity>> = noHooks()): Entity?

    fun showByFilter(filter: DynamicModelFilter, cached: Boolean = false, persistCopy: Boolean? = null, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreShowByHook<ID, Entity>, CRUDOnShowByHook<ID, Entity>, CRUDPostShowByHook<ID, Entity>> = noHooks()): Entity?

    fun showByFilter(cached: Boolean = false, persistCopy: Boolean? = null, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreShowByHook<ID, Entity>, CRUDOnShowByHook<ID, Entity>, CRUDPostShowByHook<ID, Entity>> = noHooks(), block: ModelFilterBuilder<Entity>.() -> Unit): Entity? {
        val builder = ModelFilterBuilder<Entity>()
        builder.block()
        val filter = builder.build()
        return showByFilter(filter, cached, persistCopy, applyPolicies, hooks)
    }

    fun searchByFilter(filter: DynamicModelFilter, cached: Boolean = false, persistCopy: Boolean? = null, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreIndexHook<ID, Entity>, CRUDOnIndexHook<ID, Entity>, CRUDPostIndexHook<ID, Entity>> = noHooks()): PagedResult<Entity>

    fun searchByFilter(cached: Boolean = false, persistCopy: Boolean? = null, applyPolicies: Boolean = false,  hooks: HooksDTO<CRUDPreIndexHook<ID, Entity>, CRUDOnIndexHook<ID, Entity>, CRUDPostIndexHook<ID, Entity>> = noHooks(), block: ModelFilterBuilder<Entity>.() -> Unit): PagedResult<Entity> {
        val builder = ModelFilterBuilder<Entity>()
        builder.block()
        val filter = builder.build()
        return searchByFilter(filter, cached, persistCopy, applyPolicies, hooks)
    }

    fun searchByFilterCount(filter: DynamicModelFilter, applyPolicies: Boolean = false): Long

    fun searchByFilterCount(applyPolicies: Boolean = false, block: FilterFieldsBuilder<Entity>.() -> Unit): Long {
        val builder = FilterFieldsBuilder<Entity>()
        builder.block()
        val filter = DynamicModelFilter(builder.build().toMutableList())
        return searchByFilterCount(filter, applyPolicies)
    }

    fun update(entity: Entity, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> = noHooks()): Entity

    fun updateByFilter(filter: DynamicModelFilter, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> = noHooks(), updateBlock: Entity.() -> Unit): PagedResult<Entity> {
        return searchByFilter(filter, applyPolicies = applyPolicies).mapResults {
            it.updateBlock()
            update(it, applyPolicies, hooks)
        }
    }

    fun updateByFilter(applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> = noHooks(), searchBlock: ModelFilterBuilder<Entity>.() -> Unit, updateBlock: Entity.() -> Unit): PagedResult<Entity> {
        val builder = ModelFilterBuilder<Entity>()
        builder.searchBlock()
        val filter = builder.build()
        return updateByFilter(filter, applyPolicies, hooks, updateBlock)
    }

    fun updateById(id: ID, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> = noHooks(), block: Entity.() -> Unit): Entity {
        val entity = showById(id, applyPolicies = applyPolicies) ?: error("Entity with id $id not found")
        entity.block()
        return update(entity, applyPolicies, hooks)
    }

    fun bulkUpdate(entities: List<Entity>, applyPolicies: Boolean, hooks: HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> = noHooks()): List<Entity>

    fun deleteById(id: ID, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>> = noHooks())

    fun delete(entity: Entity, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>> = noHooks()) = deleteById(entity.id, applyPolicies, hooks)

    fun deleteByFilter(filter: DynamicModelFilter, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>> = noHooks()) {
        searchByFilter(filter, applyPolicies = applyPolicies).forEach {
            delete(it, applyPolicies, hooks)
        }
    }

    fun deleteByFilter(applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>> = noHooks(), block: ModelFilterBuilder<Entity>.() -> Unit) {
        val builder = ModelFilterBuilder<Entity>()
        builder.block()
        val filter = builder.build()
        deleteByFilter(filter, applyPolicies, hooks)
    }

    fun bulkDelete(ids: List<ID>, applyPolicies: Boolean = false, hooks: HooksDTO<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>> = noHooks())

    companion object {
        fun <PreHook, OnHook, PostHook> noHooks() = HooksDTO(mutableListOf<PreHook>(), mutableListOf<OnHook>(), mutableListOf<PostHook>())
    }
}