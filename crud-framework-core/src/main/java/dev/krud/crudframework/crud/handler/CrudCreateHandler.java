package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.hooks.HooksDTO;
import dev.krud.crudframework.crud.hooks.create.CRUDOnCreateHook;
import dev.krud.crudframework.crud.hooks.create.CRUDPostCreateHook;
import dev.krud.crudframework.crud.hooks.create.CRUDPreCreateHook;
import dev.krud.crudframework.crud.hooks.create.from.CRUDOnCreateFromHook;
import dev.krud.crudframework.crud.hooks.create.from.CRUDPostCreateFromHook;
import dev.krud.crudframework.crud.hooks.create.from.CRUDPreCreateFromHook;
import dev.krud.crudframework.model.BaseCrudEntity;

import java.io.Serializable;
import java.util.List;

public interface CrudCreateHandler {
	<ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity createInternal(Entity entity, HooksDTO<CRUDPreCreateHook<ID, Entity>, CRUDOnCreateHook<ID, Entity>, CRUDPostCreateHook<ID, Entity>> hooks, boolean applyPolicies);


    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> bulkCreateInternal(List<Entity> entities, boolean applyPolicies);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity createFromInternal(Object object, Class<Entity> clazz,
                                                                                           HooksDTO<CRUDPreCreateFromHook<ID, Entity>, CRUDOnCreateFromHook<ID, Entity>, CRUDPostCreateFromHook<ID, Entity>> hooks);
}
