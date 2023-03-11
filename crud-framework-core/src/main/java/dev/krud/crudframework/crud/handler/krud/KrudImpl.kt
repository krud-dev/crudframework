package dev.krud.crudframework.crud.handler.krud

import dev.krud.crudframework.crud.handler.CrudCreateHandler
import dev.krud.crudframework.crud.handler.CrudDeleteHandler
import dev.krud.crudframework.crud.handler.CrudReadHandler
import dev.krud.crudframework.crud.handler.CrudUpdateHandler
import dev.krud.crudframework.crud.hooks.HooksDTO
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.dsl.FilterFieldsBuilder
import dev.krud.crudframework.modelfilter.dsl.ModelFilterBuilder
import dev.krud.crudframework.ro.PagedResult
import org.springframework.beans.factory.InitializingBean
import java.io.Serializable

class KrudImpl<Entity : BaseCrudEntity<ID>, ID : Serializable>(
    private val crudCreateHandler: CrudCreateHandler, private val crudReadHandler: CrudReadHandler, private val crudUpdateHandler: CrudUpdateHandler, private val crudDeleteHandler: CrudDeleteHandler
) :
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

    override fun showById(id: ID, cached: Boolean, persistCopy: Boolean, applyPolicies: Boolean): Entity? {
        return crudReadHandler.showInternal(id, entityClazz, noHooks(), cached, persistCopy, applyPolicies)
    }

    override fun showByFilter(cached: Boolean, persistCopy: Boolean, applyPolicies: Boolean, block: ModelFilterBuilder<Entity>.() -> Unit): Entity? {
        val builder = ModelFilterBuilder<Entity>()
        builder.block()
        val filter = builder.build()
        return crudReadHandler.showByInternal(filter, entityClazz, noHooks(), cached, persistCopy, applyPolicies)
    }

    override fun searchByFilter(cached: Boolean, persistCopy: Boolean, applyPolicies: Boolean, block: ModelFilterBuilder<Entity>.() -> Unit): PagedResult<Entity> {
        val builder = ModelFilterBuilder<Entity>()
        builder.block()
        val filter = builder.build()
        return crudReadHandler.indexInternal(filter, entityClazz, noHooks(), cached, persistCopy, applyPolicies, false)
    }

    override fun searchByFilterCount(applyPolicies: Boolean, block: FilterFieldsBuilder<Entity>.() -> Unit): Long {
        val builder = FilterFieldsBuilder<Entity>()
        builder.block()
        val filter = DynamicModelFilter(builder.build().toMutableList())
        return crudReadHandler.indexInternal(filter, entityClazz, noHooks(), false, false, applyPolicies, true).total
    }

    override fun update(entity: Entity, applyPolicies: Boolean): Entity {
        return crudUpdateHandler.updateInternal(entity, noHooks(), applyPolicies)
    }


    override fun delete(id: ID, applyPolicies: Boolean) {
        crudDeleteHandler.deleteInternal(id, entityClazz, noHooks(), applyPolicies)
    }

    companion object {
        private fun <PreHook, OnHook, PostHook> noHooks() =
            HooksDTO(emptyList<PreHook>(), emptyList<OnHook>(), emptyList<PostHook>())
    }
}