package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.hooks.delete.CRUDOnDeleteHook;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public interface CrudDeleteTransactionalHandler {
    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity deleteHardTransactional(DynamicModelFilter filter, Class<Entity> clazz, List<CRUDOnDeleteHook<ID, Entity>> onHooks, boolean applyPolicies);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity deleteSoftTransactional(DynamicModelFilter filter, Field deleteField, Class<Entity> clazz, List<CRUDOnDeleteHook<ID, Entity>> onHooks, boolean applyPolicies);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> bulkDeleteHardTransactional(DynamicModelFilter filter, Class<Entity> entityClazz, List<CRUDOnDeleteHook<ID,Entity>> onHooks, boolean applyPolicies);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> bulkDeleteSoftTransactional(DynamicModelFilter filter, Field deleteField, Class<Entity> entityClazz, List<CRUDOnDeleteHook<ID,Entity>> onHooks, boolean applyPolicies);
}
