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
import dev.krud.crudframework.ro.PagingDTO;
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
import dev.krud.crudframework.ro.PagingDTO;

import java.io.Serializable;
import java.util.List;

public interface CrudReadHandler {

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> PagingDTO<Entity> indexInternal(DynamicModelFilter filter, Class<Entity> clazz,
                                                                                                 HooksDTO<CRUDPreIndexHook<ID, Entity>, CRUDOnIndexHook<ID, Entity>, CRUDPostIndexHook<ID, Entity>> hooks,
                                                                                                 boolean fromCache, Boolean persistCopy, boolean applyPolicies, boolean count);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> PagingDTO<Entity> indexTransactional(DynamicModelFilter filter, Class<Entity> clazz,
                                                                                                      List<CRUDOnIndexHook<ID, Entity>> onHooks,
                                                                                                      Boolean persistCopy, boolean count, boolean applyPolicies);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showByInternal(DynamicModelFilter filter, Class<Entity> clazz,
                                                                                       HooksDTO<CRUDPreShowByHook<ID, Entity>, CRUDOnShowByHook<ID, Entity>, CRUDPostShowByHook<ID, Entity>> hooks, boolean fromCache, Boolean persistCopy, boolean applyPolicies);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showByTransactional(DynamicModelFilter filter, Class<Entity> clazz, List<CRUDOnShowByHook<ID, Entity>> onHooks,
                                                                                            Boolean persistCopy, boolean applyPolicies);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showInternal(ID id, Class<Entity> clazz,
                                                                                     HooksDTO<CRUDPreShowHook<ID, Entity>, CRUDOnShowHook<ID, Entity>, CRUDPostShowHook<ID, Entity>> hooks, boolean fromCache, Boolean persistCopy, boolean applyPolicies);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showTransactional(DynamicModelFilter filter, Class<Entity> clazz, List<CRUDOnShowHook<ID, Entity>> onHooks, Boolean persistCopy, boolean applyPolicies);
}
