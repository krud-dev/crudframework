package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.cache.CrudCache;
import dev.krud.crudframework.crud.hooks.interfaces.CRUDHooks;
import dev.krud.crudframework.crud.model.EntityMetadataDTO;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import dev.krud.crudframework.modelfilter.FilterField;
import dev.krud.crudframework.crud.cache.CrudCache;
import dev.krud.crudframework.crud.hooks.interfaces.CRUDHooks;
import dev.krud.crudframework.crud.model.EntityMetadataDTO;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import dev.krud.crudframework.modelfilter.FilterField;

import java.io.Serializable;
import java.util.List;

public interface CrudHelper {

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>, HooksType extends CRUDHooks> List<HooksType> getHooks(Class<HooksType> crudHooksClazz, Class<Entity> entityClazz);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> boolean isEntityDeleted(Entity entity);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void decorateFilter(DynamicModelFilter filter, Class<Entity> entityClazz);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void validateAndFillFilterFieldMetadata(List<FilterField> filterFields, Class<Entity> entityClazz);

    /* transactional */
    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> getEntities(DynamicModelFilter filter, Class<Entity> entityClazz, Boolean persistCopy);

    /* transactional */
    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> long getEntitiesCount(DynamicModelFilter filter, Class<Entity> entityClazz, boolean forUpdate);

    /* transactional */
    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity getEntity(DynamicModelFilter filter, Class<Entity> entityClazz, Boolean persistCopy);

    /* transactional */
    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> long getEntityCountById(ID entityId, Class<Entity> entityClazz, boolean forUpdate);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void checkEntityImmutability(Class<Entity> clazz);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void checkEntityDeletability(Class<Entity> clazz);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> EntityMetadataDTO getEntityMetadata(Class<Entity> entityClazz);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void evictEntityFromCache(Entity entity);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> CrudCache getEntityCache(Class<Entity> clazz);

    void validate(Object target);


    <From, To> To fill(From fromObject, Class<To> toClazz);

    <From, To> void fill(From fromObject, To toObject);

    <From, To> List<To> fillMany(List<From> fromObjects, Class<To> toClazz);

    <Entity> void setTotalToPagingCache(Class<Entity> entityClazz, DynamicModelFilter filter, long total);

    <Entity> Long getTotalFromPagingCache(Class<Entity> entityClazz, DynamicModelFilter filter);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> CrudDao getCrudDaoForEntity(Class<Entity> entityClazz);
}
