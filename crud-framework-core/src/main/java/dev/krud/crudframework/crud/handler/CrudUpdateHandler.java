package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.hooks.HooksDTO;
import dev.krud.crudframework.crud.hooks.update.CRUDOnUpdateHook;
import dev.krud.crudframework.crud.hooks.update.CRUDPostUpdateHook;
import dev.krud.crudframework.crud.hooks.update.CRUDPreUpdateHook;
import dev.krud.crudframework.crud.hooks.update.from.CRUDOnUpdateFromHook;
import dev.krud.crudframework.crud.hooks.update.from.CRUDPostUpdateFromHook;
import dev.krud.crudframework.crud.hooks.update.from.CRUDPreUpdateFromHook;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;

import java.io.Serializable;
import java.util.List;

public interface CrudUpdateHandler {

	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateInternal(Entity entity, HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> hooks, boolean applyPolicies);

	default <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateTransactional(Entity entity, List<CRUDOnUpdateHook<ID, Entity>> onHooks) {
		return updateTransactional(entity, onHooks);
	}

	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateTransactional(Entity entity, DynamicModelFilter filter, List<CRUDOnUpdateHook<ID, Entity>> onHooks, boolean applyPolicies);

	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateFromInternal(ID id, Object object, Class<Entity> clazz,
																						   HooksDTO<CRUDPreUpdateFromHook<ID, Entity>, CRUDOnUpdateFromHook<ID, Entity>, CRUDPostUpdateFromHook<ID, Entity>> hooks, boolean applyPolicies);

	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateFromTransactional(DynamicModelFilter filter, Object object, Class<Entity> clazz, List<CRUDOnUpdateFromHook<ID, Entity>> onHooks, boolean applyPolicies);

	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> updateManyTransactional(List<Entity> entities,
																									  HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> hooks, Boolean persistCopy, boolean applyPolicies);

	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> updateByFilterTransactional(DynamicModelFilter filter, Class<Entity> entityClazz,
																										  HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> hooks, Boolean persistCopy, boolean applyPolicies);
}
