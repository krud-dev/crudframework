package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.hooks.interfaces.FieldChangeHook;
import dev.krud.crudframework.crud.hooks.update.CRUDOnUpdateHook;
import dev.krud.crudframework.crud.hooks.update.from.CRUDOnUpdateFromHook;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;

import java.io.Serializable;
import java.util.List;

public interface CrudUpdateTransactionalHandler {

	default <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateTransactional(Entity entity, List<CRUDOnUpdateHook<ID, Entity>> onHooks, List<FieldChangeHook> fieldChangeHooks) {
		return updateTransactional(entity, onHooks, fieldChangeHooks);
	}

	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateTransactional(Entity entity, DynamicModelFilter filter, List<CRUDOnUpdateHook<ID, Entity>> onHooks, List<FieldChangeHook> fieldChangeHooks, boolean applyPolicies);
	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateFromTransactional(DynamicModelFilter filter, Object object, Class<Entity> clazz, List<CRUDOnUpdateFromHook<ID, Entity>> onHooks, List<FieldChangeHook> fieldChangeHooks, boolean applyPolicies);
}
