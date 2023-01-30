package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.exception.CrudUpdateException;
import dev.krud.crudframework.crud.hooks.update.CRUDOnUpdateHook;
import dev.krud.crudframework.crud.hooks.update.from.CRUDOnUpdateFromHook;
import dev.krud.crudframework.crud.policy.PolicyRuleType;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public class CrudUpdateTransactionalHandlerImpl implements CrudUpdateTransactionalHandler {
    private final CrudHelper crudHelper;

    private final CrudSecurityHandler crudSecurityHandler;

    public CrudUpdateTransactionalHandlerImpl(CrudHelper crudHelper, CrudSecurityHandler crudSecurityHandler) {
        this.crudHelper = crudHelper;
        this.crudSecurityHandler = crudSecurityHandler;
    }

    @Override
    @Transactional(readOnly = false)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateTransactional(Entity entity, DynamicModelFilter filter, List<CRUDOnUpdateHook<ID, Entity>> onHooks, boolean applyPolicies) {
        // check id exists and has access to entity
        Entity existingEntity = crudHelper.getEntity(filter, (Class<Entity>) entity.getClass(), true);

        if (existingEntity == null) {
            throw new CrudUpdateException("Entity of type [ " + entity.getClass().getSimpleName() + " ] does not exist or cannot be updated");
        }

        if (applyPolicies) {
            crudSecurityHandler.evaluatePostRulesAndThrow(existingEntity, PolicyRuleType.CAN_UPDATE, entity.getClass());
        }

        for (CRUDOnUpdateHook<ID, Entity> onHook : onHooks) {
            onHook.run(entity);
        }

        crudHelper.validate(entity);

        return crudHelper.getCrudDaoForEntity(entity.getClass()).saveOrUpdate(entity);
    }

    @Override
    @Transactional(readOnly = false)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateFromTransactional(DynamicModelFilter filter, Object object, Class<Entity> clazz, List<CRUDOnUpdateFromHook<ID, Entity>> onHooks, boolean applyPolicies) {
        Entity entity = crudHelper.getEntity(filter, clazz, null);

        if (entity == null) {
            throw new CrudUpdateException("Entity of type [ " + clazz.getSimpleName() + " ] does not exist or cannot be updated");
        }

        if (applyPolicies) {
            crudSecurityHandler.evaluatePostRulesAndThrow(entity, PolicyRuleType.CAN_UPDATE, clazz);
        }

        crudHelper.fill(object, entity);

        for (CRUDOnUpdateFromHook<ID, Entity> onHook : onHooks) {
            onHook.run(entity, object);
        }

        crudHelper.validate(entity);

        return crudHelper.getCrudDaoForEntity(clazz).saveOrUpdate(entity);
    }
}
