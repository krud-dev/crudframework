package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.hooks.HooksDTO;
import dev.krud.crudframework.crud.hooks.index.CRUDOnIndexHook;
import dev.krud.crudframework.crud.hooks.index.CRUDPostIndexHook;
import dev.krud.crudframework.crud.hooks.index.CRUDPreIndexHook;
import dev.krud.crudframework.crud.hooks.show.CRUDOnShowHook;
import dev.krud.crudframework.crud.hooks.show.CRUDPostShowHook;
import dev.krud.crudframework.crud.hooks.show.CRUDPreShowHook;
import dev.krud.crudframework.crud.hooks.show.by.CRUDOnShowByHook;
import dev.krud.crudframework.crud.hooks.show.by.CRUDPostShowByHook;
import dev.krud.crudframework.crud.hooks.show.by.CRUDPreShowByHook;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import dev.krud.crudframework.ro.PagedResult;

import java.io.Serializable;
import java.util.List;

public interface CrudReadTransactionalHandler {

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> PagedResult<Entity> indexTransactional(DynamicModelFilter filter, Class<Entity> clazz,
                                                                                                        List<CRUDOnIndexHook<ID, Entity>> onHooks,
                                                                                                        Boolean persistCopy, boolean count, boolean applyPolicies);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showByTransactional(DynamicModelFilter filter, Class<Entity> clazz, List<CRUDOnShowByHook<ID, Entity>> onHooks,
                                                                                            Boolean persistCopy, boolean applyPolicies);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showTransactional(DynamicModelFilter filter, Class<Entity> clazz, List<CRUDOnShowHook<ID, Entity>> onHooks, Boolean persistCopy, boolean applyPolicies);
}
