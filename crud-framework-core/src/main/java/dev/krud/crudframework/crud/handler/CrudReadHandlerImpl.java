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
import java.util.List;
import java.util.Random;

@WrapException(CrudReadException.class)
public class CrudReadHandlerImpl implements CrudReadHandler {

    @Autowired
    private CrudHelper crudHelper;

    @Autowired
    private CrudReadTransactionalHandler crudReadTransactionalHandler;

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
        PagedResult<Entity> result = (PagedResult<Entity>) CacheUtils.getObjectAndCache(() -> crudReadTransactionalHandler.indexTransactional(finalFilter, clazz, hooks.getOnHooks(), persistCopy, count, applyPolicies), cacheKey, cache);

        for (CRUDPostIndexHook<ID, Entity> postHook : hooks.getPostHooks()) {
            postHook.run(filter, result);
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
        Entity entity = (Entity) CacheUtils.getObjectAndCache(() -> crudReadTransactionalHandler.showByTransactional(finalFilter, clazz, hooks.getOnHooks(), persistCopy, applyPolicies), "showBy_" + filter.hashCode(), cache);

        for (CRUDPostShowByHook<ID, Entity> postHook : hooks.getPostHooks()) {
            postHook.run(entity);
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

        Entity entity = (Entity) CacheUtils.getObjectAndCache(() -> crudReadTransactionalHandler.showTransactional(filter, clazz, hooks.getOnHooks(), persistCopy, applyPolicies), BaseCrudEntity.Companion.getCacheKey(clazz, id), cache);

        for (CRUDPostShowHook<ID, Entity> postHook : hooks.getPostHooks()) {
            postHook.run(entity);
        }

        return entity;
    }
}
