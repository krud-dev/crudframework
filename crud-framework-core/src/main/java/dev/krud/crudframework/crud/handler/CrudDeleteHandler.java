package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.hooks.HooksDTO;
import dev.krud.crudframework.crud.hooks.delete.CRUDOnDeleteHook;
import dev.krud.crudframework.crud.hooks.delete.CRUDPostDeleteHook;
import dev.krud.crudframework.crud.hooks.delete.CRUDPreDeleteHook;
import dev.krud.crudframework.model.BaseCrudEntity;

import java.io.Serializable;

public interface CrudDeleteHandler {
    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void deleteInternal(ID id, Class<Entity> clazz,
                                                                                     HooksDTO<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>> hooks, boolean applyPolicies);
}
