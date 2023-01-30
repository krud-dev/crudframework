package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.cache.CacheUtils;
import dev.krud.crudframework.crud.cache.CrudCache;
import dev.krud.crudframework.crud.exception.CrudReadException;
import dev.krud.crudframework.crud.hooks.HooksDTO;
import dev.krud.crudframework.crud.hooks.index.CRUDOnIndexHook;
import dev.krud.crudframework.crud.hooks.index.CRUDPostIndexHook;
import dev.krud.crudframework.crud.hooks.index.CRUDPreIndexHook;
import dev.krud.crudframework.crud.hooks.interfaces.IndexHooks;
import dev.krud.crudframework.crud.hooks.interfaces.ShowByHooks;
import dev.krud.crudframework.crud.hooks.interfaces.ShowHooks;
import dev.krud.crudframework.crud.hooks.show.CRUDOnShowHook;
import dev.krud.crudframework.crud.hooks.show.CRUDPostShowHook;
import dev.krud.crudframework.crud.hooks.show.CRUDPreShowHook;
import dev.krud.crudframework.crud.hooks.show.by.CRUDOnShowByHook;
import dev.krud.crudframework.crud.hooks.show.by.CRUDPostShowByHook;
import dev.krud.crudframework.crud.hooks.show.by.CRUDPreShowByHook;
import dev.krud.crudframework.crud.policy.PolicyRuleType;
import dev.krud.crudframework.exception.WrapException;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import dev.krud.crudframework.modelfilter.FilterFields;
import dev.krud.crudframework.modelfilter.enums.FilterFieldDataType;
import dev.krud.crudframework.ro.PagedResult;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@WrapException(CrudReadException.class)
public class CrudReadHandlerImpl implements CrudReadHandler {

    @Autowired
    private CrudHelper crudHelper;

    @Resource(name = "crudReadHandler")
    private CrudReadHandler crudReadHandlerProxy;

    @Autowired
    private CrudSecurityHandler crudSecurityHandler;

    private static Random random = new Random();

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> PagedResult<Entity> indexInternal(DynamicModelFilter filter, Class<Entity> clazz,
                                                                                                          HooksDTO<CRUDPreIndexHook<ID, Entity>, CRUDOnIndexHook<ID, Entity>, CRUDPostIndexHook<ID, Entity>> hooks,
                                                                                                          boolean fromCache, Boolean persistCopy, boolean applyPolicies, boolean count) {
        if (filter == null) {
            filter = new DynamicModelFilter();
        }

        if (applyPolicies) {
            crudSecurityHandler.evaluatePreRulesAndThrow(PolicyRuleType.CAN_ACCESS, clazz);
            crudSecurityHandler.decorateFilter(clazz, filter);
        }


        crudHelper.validateAndFillFilterFieldMetadata(filter.getFilterFields(), clazz);
        List<IndexHooks> indexHooksList = crudHelper.getHooks(IndexHooks.class, clazz);

        if (indexHooksList != null && !indexHooksList.isEmpty()) {
            for (IndexHooks<ID, Entity> indexHooks : indexHooksList) {
                hooks.getPreHooks().add(0, indexHooks::preIndex);
                hooks.getOnHooks().add(0, indexHooks::onIndex);
                hooks.getPostHooks().add(0, indexHooks::postIndex);
            }
        }

        CrudCache cache = null;

        if (fromCache) {
            cache = crudHelper.getEntityCache(clazz);
        }

        for (CRUDPreIndexHook<ID, Entity> preHook : hooks.getPreHooks()) {
            preHook.run(filter);
        }

        String cacheKey = filter.getCacheKey();
        if (count) {
            cacheKey = "count_" + cacheKey;
        }


        DynamicModelFilter finalFilter = filter;
        PagedResult<Entity> result = (PagedResult<Entity>) CacheUtils.getObjectAndCache(() -> crudReadHandlerProxy.indexTransactional(finalFilter, clazz, hooks.getOnHooks(), persistCopy, count, applyPolicies), cacheKey, cache);

        for (CRUDPostIndexHook<ID, Entity> postHook : hooks.getPostHooks()) {
            postHook.run(filter, result);
        }

        return result;
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

            result =  new PagedResult<>(filter.getStart(), filter.getLimit(), total, hasMore, entities);
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
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showByInternal(DynamicModelFilter filter, Class<Entity> clazz,
                                                                                              HooksDTO<CRUDPreShowByHook<ID, Entity>, CRUDOnShowByHook<ID, Entity>, CRUDPostShowByHook<ID, Entity>> hooks, boolean fromCache, Boolean persistCopy, boolean applyPolicies) {

        if (filter == null) {
            filter = new DynamicModelFilter();
        }

        if (applyPolicies) {
            crudSecurityHandler.evaluatePreRulesAndThrow(PolicyRuleType.CAN_ACCESS, clazz);
            crudSecurityHandler.decorateFilter(clazz, filter);
        }

        crudHelper.validateAndFillFilterFieldMetadata(filter.getFilterFields(), clazz);
        List<ShowByHooks> showByHooksList = crudHelper.getHooks(ShowByHooks.class, clazz);

        if (showByHooksList != null && !showByHooksList.isEmpty()) {
            for (ShowByHooks<ID, Entity> showByHooks : showByHooksList) {
                hooks.getPreHooks().add(0, showByHooks::preShowBy);
                hooks.getOnHooks().add(0, showByHooks::onShowBy);
                hooks.getPostHooks().add(0, showByHooks::postShowBy);
            }
        }

        for (CRUDPreShowByHook<ID, Entity> preHook : hooks.getPreHooks()) {
            preHook.run(filter);
        }

        CrudCache cache = null;
        if (fromCache) {
            cache = crudHelper.getEntityCache(clazz);
        }

        DynamicModelFilter finalFilter = filter;
        Entity entity = (Entity) CacheUtils.getObjectAndCache(() -> crudReadHandlerProxy.showByTransactional(finalFilter, clazz, hooks.getOnHooks(), persistCopy, applyPolicies), "showBy_" + filter.hashCode(), cache);

        for (CRUDPostShowByHook<ID, Entity> postHook : hooks.getPostHooks()) {
            postHook.run(entity);
        }

        return entity;
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
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity showInternal(ID id, Class<Entity> clazz,
                                                                                            HooksDTO<CRUDPreShowHook<ID, Entity>, CRUDOnShowHook<ID, Entity>, CRUDPostShowHook<ID, Entity>> hooks, boolean fromCache, Boolean persistCopy, boolean applyPolicies) {
        DynamicModelFilter filter = new DynamicModelFilter()
                .add(FilterFields.eq("id", FilterFieldDataType.get(id.getClass()), id));
        if (applyPolicies) {
            crudSecurityHandler.evaluatePreRulesAndThrow(PolicyRuleType.CAN_ACCESS, clazz);
            crudSecurityHandler.decorateFilter(clazz, filter);
        }
        List<ShowHooks> showHooksList = crudHelper.getHooks(ShowHooks.class, clazz);

        if (showHooksList != null && !showHooksList.isEmpty()) {
            for (ShowHooks<ID, Entity> showHooks : showHooksList) {
                hooks.getPreHooks().add(0, showHooks::preShow);
                hooks.getOnHooks().add(0, showHooks::onShow);
                hooks.getPostHooks().add(0, showHooks::postShow);
            }
        }

        for (CRUDPreShowHook<ID, Entity> preHook : hooks.getPreHooks()) {
            preHook.run(id);
        }

        CrudCache cache = null;
        if (fromCache) {
            cache = crudHelper.getEntityCache(clazz);
        }

        Entity entity = (Entity) CacheUtils.getObjectAndCache(() -> crudReadHandlerProxy.showTransactional(filter, clazz, hooks.getOnHooks(), persistCopy, applyPolicies), BaseCrudEntity.Companion.getCacheKey(clazz, id), cache);

        for (CRUDPostShowHook<ID, Entity> postHook : hooks.getPostHooks()) {
            postHook.run(entity);
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
