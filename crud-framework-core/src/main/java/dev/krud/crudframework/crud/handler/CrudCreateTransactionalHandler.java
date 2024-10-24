package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.hooks.create.CRUDOnCreateHook;
import dev.krud.crudframework.crud.hooks.create.from.CRUDOnCreateFromHook;
import dev.krud.crudframework.crud.hooks.interfaces.CreateHooks;
import dev.krud.crudframework.crud.hooks.interfaces.FieldChangeHook;
import dev.krud.crudframework.crud.hooks.interfaces.FieldChangeHooks;
import dev.krud.crudframework.model.BaseCrudEntity;

import java.io.Serializable;
import java.util.List;

public interface CrudCreateTransactionalHandler {
    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity createTransactional(Entity entity, List<CRUDOnCreateHook<ID, Entity>> onHooks, List<FieldChangeHook> fieldChangeHooks);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity createFromTransactional(Object object, Class<Entity> clazz, List<CRUDOnCreateFromHook<ID, Entity>> onHooks, List<FieldChangeHook> fieldChangeHooks);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> bulkCreateTransactional(List<Entity> entities, List<CreateHooks> hooks, List<FieldChangeHook> fieldChangeHooks);
}
