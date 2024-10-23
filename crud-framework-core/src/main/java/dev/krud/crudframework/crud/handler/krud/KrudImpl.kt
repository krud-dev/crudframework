package dev.krud.crudframework.crud.handler.krud

import dev.krud.crudframework.crud.handler.CrudCreateHandler
import dev.krud.crudframework.crud.handler.CrudDeleteHandler
import dev.krud.crudframework.crud.handler.CrudReadHandler
import dev.krud.crudframework.crud.handler.CrudUpdateHandler
import dev.krud.crudframework.crud.hooks.HooksDTO
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.ro.PagedResult
import org.springframework.beans.factory.InitializingBean
import java.io.Serializable

open class KrudImpl<Entity : BaseCrudEntity<ID>, ID : Serializable>(
        private val crudCreateHandler: CrudCreateHandler, private val crudReadHandler: CrudReadHandler, private val crudUpdateHandler: CrudUpdateHandler, private val crudDeleteHandler: CrudDeleteHandler) :
        InitializingBean, Krud<Entity, ID> {
    override lateinit var entityClazz: Class<Entity>

    override fun afterPropertiesSet() {
        if (!this::entityClazz.isInitialized) {
            error("entityClazz must be initialized")
        }
    }

    override fun create(entity: Entity, applyPolicies: Boolean): Entity {
        return crudCreateHandler.createInternal(entity, noHooks(), applyPolicies)
    }

    override fun bulkCreate(entities: List<Entity>, applyPolicies: Boolean): List<Entity> {
        return crudCreateHandler.bulkCreateInternal(entities, applyPolicies)
    }

    override fun showById(id: ID, cached: Boolean, persistCopy: Boolean?, applyPolicies: Boolean): Entity? {
        return crudReadHandler.showInternal(id, entityClazz, noHooks(), cached, persistCopy, applyPolicies)
    }

    override fun showByFilter(filter: DynamicModelFilter, cached: Boolean, persistCopy: Boolean?, applyPolicies: Boolean): Entity? {
        return crudReadHandler.showByInternal(filter, entityClazz, noHooks(), cached, persistCopy, applyPolicies)
    }

    override fun searchByFilter(filter: DynamicModelFilter, cached: Boolean, persistCopy: Boolean?, applyPolicies: Boolean): PagedResult<Entity> {
        return crudReadHandler.indexInternal(filter, entityClazz, noHooks(), cached, persistCopy, applyPolicies, false)
    }

    override fun searchByFilterCount(filter: DynamicModelFilter, applyPolicies: Boolean): Long {
        return crudReadHandler.indexInternal(filter, entityClazz, noHooks(), false, false, applyPolicies, true).total
    }

    override fun update(entity: Entity, applyPolicies: Boolean): Entity {
        return crudUpdateHandler.updateInternal(entity, noHooks(), applyPolicies)
    }

    override fun bulkUpdate(entities: List<Entity>, applyPolicies: Boolean): List<Entity> {
        return crudUpdateHandler.bulkUpdate(entities, noHooks(), applyPolicies)
    }

    override fun deleteById(id: ID, applyPolicies: Boolean) {
        crudDeleteHandler.deleteInternal(id, entityClazz, noHooks(), applyPolicies)
    }

    override fun bulkDelete(ids: List<ID>, applyPolicies: Boolean) {
        crudDeleteHandler.bulkDelete(ids, entityClazz, noHooks(), applyPolicies)
    }

    companion object {
        private fun <PreHook, OnHook, PostHook> noHooks() = HooksDTO(mutableListOf<PreHook>(), mutableListOf<OnHook>(), mutableListOf<PostHook>())
    }
}