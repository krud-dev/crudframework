package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.hooks.index.CRUDOnIndexHook;
import dev.krud.crudframework.crud.hooks.show.CRUDOnShowHook;
import dev.krud.crudframework.crud.hooks.show.by.CRUDOnShowByHook;
import dev.krud.crudframework.crud.policy.PolicyRuleType;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import dev.krud.crudframework.ro.PagedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class CrudReadTransactionalHandlerImpl implements CrudReadTransactionalHandler {
    private final CrudHelper crudHelper;

    private final CrudSecurityHandler crudSecurityHandler;

    public CrudReadTransactionalHandlerImpl(CrudHelper crudHelper, CrudSecurityHandler crudSecurityHandler) {
        this.crudHelper = crudHelper;
        this.crudSecurityHandler = crudSecurityHandler;
    }

    @Override
    @Transactional(readOnly = true)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> PagedResult<Entity> indexTransactional(DynamicModelFilter filter, Class<Entity> clazz,
                                                                                                               List<CRUDOnIndexHook<ID, Entity>> onHooks,
                                                                                                               Boolean persistCopy, boolean count, boolean applyPolicies) {
        PagedResult<Entity> result;
        if (!count) {
            long total;
            List<Entity> entities;
            boolean hasMore;

            if (filter.getLimit() != null) {
                filter.setLimit(filter.getLimit() + 1);
                entities = crudHelper.getEntities(filter, clazz, persistCopy);

                if (applyPolicies) {
                    for (Entity entity : entities) {
                        crudSecurityHandler.evaluatePostRulesAndThrow(entity, PolicyRuleType.CAN_ACCESS, clazz);
                    }
                }
                hasMore = entities.size() == filter.getLimit();
                filter.setLimit(filter.getLimit() - 1);
                long start = filter.getStart() == null ? 0 : filter.getStart();
                if (hasMore) {
                    entities.remove(entities.size() - 1);
                } else {
                    crudHelper.setTotalToPagingCache(clazz, filter, entities.size() + start);
                }

                Long cachedTotal = crudHelper.getTotalFromPagingCache(clazz, filter);
                if (cachedTotal != null) {
                    hasMore = false;
                    total = cachedTotal;
                } else {
                    total = entities.size() + start;
                }
            } else {
                entities = crudHelper.getEntities(filter, clazz, persistCopy);
                hasMore = false;
                total = entities.size();
                crudHelper.setTotalToPagingCache(clazz, filter, total);

            }

            result = new PagedResult<>(filter.getStart(), filter.getLimit(), total, hasMore, entities);
        } else {
            long total = crudHelper.getEntitiesCount(filter, clazz, false);
            result = new PagedResult<>(null, null, total, false, Collections.emptyList());
            crudHelper.setTotalToPagingCache(clazz, filter, total);
        }

        for (CRUDOnIndexHook<ID, Entity> onHook : onHooks) {
            onHook.run(filter, result);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showByTransactional(DynamicModelFilter filter, Class<Entity> clazz, List<CRUDOnShowByHook<ID, Entity>> onHooks,
                                                                                                   Boolean persistCopy, boolean applyPolicies) {
        List<Entity> entities = crudHelper.getEntities(filter, clazz, persistCopy);
        Entity entity = null;
        if (entities.size() > 0) {
            entity = entities.get(0);
        }

        if (applyPolicies) {
            crudSecurityHandler.evaluatePostRulesAndThrow(entity, PolicyRuleType.CAN_ACCESS, clazz);
        }

        for (CRUDOnShowByHook<ID, Entity> onHook : onHooks) {
            onHook.run(entity);
        }

        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showTransactional(DynamicModelFilter filter, Class<Entity> clazz, List<CRUDOnShowHook<ID, Entity>> onHooks, Boolean persistCopy, boolean applyPolicies) {
        Entity entity = crudHelper.getEntity(filter, clazz, persistCopy);

        if (applyPolicies) {
            crudSecurityHandler.evaluatePostRulesAndThrow(entity, PolicyRuleType.CAN_ACCESS, clazz);
        }

        for (CRUDOnShowHook<ID, Entity> onHook : onHooks) {
            onHook.run(entity);
        }

        return entity;
    }
}
