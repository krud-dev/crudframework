package dev.krud.crudframework.crud.handler

import dev.krud.crudframework.crud.cache.CrudCache
import dev.krud.crudframework.crud.hooks.interfaces.CRUDHooks
import dev.krud.crudframework.crud.model.EntityMetadataDTO
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.FilterField
import java.io.Serializable

abstract class AbstractCrudHelper : CrudHelper {
    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?, HooksType : CRUDHooks<*, out BaseCrudEntity<*>>?> getHooks(crudHooksClazz: Class<HooksType>?, entityClazz: Class<Entity>?): MutableList<HooksType> {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> isEntityDeleted(entity: Entity): Boolean {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> decorateFilter(
        filter: DynamicModelFilter?,
        entityClazz: Class<Entity>?
    ) {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> validateAndFillFilterFieldMetadata(
        filterFields: MutableList<FilterField>?,
        entityClazz: Class<Entity>?
    ) {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> getEntities(
        filter: DynamicModelFilter?,
        entityClazz: Class<Entity>?,
        persistCopy: Boolean?
    ): MutableList<Entity> {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> getEntitiesCount(
        filter: DynamicModelFilter?,
        entityClazz: Class<Entity>?,
        forUpdate: Boolean
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> getEntity(
        filter: DynamicModelFilter,
        entityClazz: Class<Entity>?,
        persistCopy: Boolean?
    ): Entity {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> getEntityCountById(
        entityId: ID,
        entityClazz: Class<Entity>?,
        forUpdate: Boolean
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> checkEntityImmutability(clazz: Class<Entity>?) {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> checkEntityDeletability(clazz: Class<Entity>?) {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> getEntityMetadata(entityClazz: Class<Entity>?): EntityMetadataDTO {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> evictEntityFromCache(entity: Entity) {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> getEntityCache(clazz: Class<Entity>?): CrudCache {
        throw UnsupportedOperationException()
    }

    override fun <From : Any?, To : Any?> fill(fromObject: From, toClazz: Class<To>): To {
        throw UnsupportedOperationException()
    }

    override fun <From : Any?, To : Any?> fill(fromObject: From, toObject: To) {
        throw UnsupportedOperationException()
    }

    override fun <From : Any?, To : Any?> fillMany(fromObjects: MutableList<From>, toClazz: Class<To>): List<To> {
        throw UnsupportedOperationException()
    }

    override fun <Entity : Any?> setTotalToPagingCache(entityClazz: Class<Entity>?, filter: DynamicModelFilter?, total: Long) {
        throw UnsupportedOperationException()
    }

    override fun <Entity : Any?> getTotalFromPagingCache(entityClazz: Class<Entity>?, filter: DynamicModelFilter?): Long {
        throw UnsupportedOperationException()
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> getCrudDaoForEntity(entityClazz: Class<Entity>?): CrudDao {
        throw UnsupportedOperationException()
    }
}