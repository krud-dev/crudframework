package dev.krud.crudframework.crud.handler.krud

import dev.krud.crudframework.crud.handler.CrudCreateHandler
import dev.krud.crudframework.crud.handler.CrudDeleteHandler
import dev.krud.crudframework.crud.handler.CrudReadHandler
import dev.krud.crudframework.crud.handler.CrudUpdateHandler
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

    override fun create(entity: Entity, applyPolicies: Boolean, hooks: HooksDTO<CRUDPreCreateHook<ID, Entity>, CRUDOnCreateHook<ID, Entity>, CRUDPostCreateHook<ID, Entity>>): Entity {
        return crudCreateHandler.createInternal(entity, hooks, applyPolicies)
    }

    override fun bulkCreate(entities: List<Entity>, applyPolicies: Boolean): List<Entity> {
        return crudCreateHandler.bulkCreateInternal(entities, applyPolicies)
    }

    override fun showById(id: ID, cached: Boolean, persistCopy: Boolean?, applyPolicies: Boolean, hooks: HooksDTO<CRUDPreShowHook<ID, Entity>, CRUDOnShowHook<ID, Entity>, CRUDPostShowHook<ID, Entity>>): Entity? {
        return crudReadHandler.showInternal(id, entityClazz, hooks, cached, persistCopy, applyPolicies)
    }

    override fun showByFilter(filter: DynamicModelFilter, cached: Boolean, persistCopy: Boolean?, applyPolicies: Boolean, hooks: HooksDTO<CRUDPreShowByHook<ID, Entity>, CRUDOnShowByHook<ID, Entity>, CRUDPostShowByHook<ID, Entity>>): Entity? {
        return crudReadHandler.showByInternal(filter, entityClazz, hooks, cached, persistCopy, applyPolicies)
    }

    override fun searchByFilter(filter: DynamicModelFilter, cached: Boolean, persistCopy: Boolean?, applyPolicies: Boolean, hooks: HooksDTO<CRUDPreIndexHook<ID, Entity>, CRUDOnIndexHook<ID, Entity>, CRUDPostIndexHook<ID, Entity>>): PagedResult<Entity> {
        return crudReadHandler.indexInternal(filter, entityClazz, hooks, cached, persistCopy, applyPolicies, false)
    }

    override fun searchByFilterCount(filter: DynamicModelFilter, applyPolicies: Boolean): Long {
        return crudReadHandler.indexInternal(filter, entityClazz, Krud.noHooks(), false, false, applyPolicies, true).total
    }

    override fun update(entity: Entity, applyPolicies: Boolean, hooks: HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>>): Entity {
        return crudUpdateHandler.updateInternal(entity, hooks, applyPolicies)
    }

    override fun bulkUpdate(entities: List<Entity>, applyPolicies: Boolean, hooks: HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>>): List<Entity> {
        return crudUpdateHandler.bulkUpdate(entities, hooks, applyPolicies)
    }

    override fun deleteById(id: ID, applyPolicies: Boolean, hooks: HooksDTO<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>>) {
        crudDeleteHandler.deleteInternal(id, entityClazz, hooks, applyPolicies)
    }

    override fun bulkDelete(ids: List<ID>, applyPolicies: Boolean, hooks: HooksDTO<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>>) {
        crudDeleteHandler.bulkDelete(ids, entityClazz, hooks, applyPolicies)
    }
}