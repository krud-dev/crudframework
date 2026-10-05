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

public interface CrudReadHandler {

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> PagedResult<Entity> indexInternal(DynamicModelFilter filter, Class<Entity> clazz,
                                                                                                   HooksDTO<CRUDPreIndexHook<ID, Entity>, CRUDOnIndexHook<ID, Entity>, CRUDPostIndexHook<ID, Entity>> hooks,
                                                                                                   boolean fromCache, Boolean persistCopy, boolean applyPolicies, boolean count);

    /**
     * Builds the filter an index read of {@code clazz} actually runs with for the current principal: the given filter fields,
     * the {@code CAN_ACCESS} policy filter fields, the changes made by the entity's {@link dev.krud.crudframework.crud.hooks.interfaces.IndexHooks#preIndex}
     * hooks, and the soft delete filter. Applying the result to a query of {@code clazz} restricts it to what an index read could return.
     * <p>
     * Throws if the current principal fails the {@code CAN_ACCESS} pre rules, or if a {@code CAN_ACCESS} policy of {@code clazz} has post conditions,
     * since those run against loaded entities and cannot be expressed as a filter.
     * <p>
     * The given filter is not modified. Its start, limit and orders are not carried over.
     */
    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> DynamicModelFilter buildEffectiveReadFilter(DynamicModelFilter filter, Class<Entity> clazz);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showByInternal(DynamicModelFilter filter, Class<Entity> clazz,
                                                                                       HooksDTO<CRUDPreShowByHook<ID, Entity>, CRUDOnShowByHook<ID, Entity>, CRUDPostShowByHook<ID, Entity>> hooks, boolean fromCache, Boolean persistCopy, boolean applyPolicies);

    <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showInternal(ID id, Class<Entity> clazz,
                                                                                     HooksDTO<CRUDPreShowHook<ID, Entity>, CRUDOnShowHook<ID, Entity>, CRUDPostShowHook<ID, Entity>> hooks, boolean fromCache, Boolean persistCopy, boolean applyPolicies);

}
