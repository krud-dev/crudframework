package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.hooks.HooksDTO;
import dev.krud.crudframework.crud.hooks.delete.CRUDOnDeleteHook;
import dev.krud.crudframework.crud.hooks.delete.CRUDPostDeleteHook;
import dev.krud.crudframework.crud.hooks.delete.CRUDPreDeleteHook;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public interface CrudDeleteHandler {

	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> void deleteInternal(ID id, Class<Entity> clazz,
																					 HooksDTO<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>> hooks, boolean applyPolicies);

	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity deleteHardTransactional(DynamicModelFilter filter, Class<Entity> clazz, List<CRUDOnDeleteHook<ID, Entity>> onHooks, boolean applyPolicies);

	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity deleteSoftTransactional(DynamicModelFilter filter, Field deleteField, Class<Entity> clazz, List<CRUDOnDeleteHook<ID, Entity>> onHooks, boolean applyPolicies);
}
