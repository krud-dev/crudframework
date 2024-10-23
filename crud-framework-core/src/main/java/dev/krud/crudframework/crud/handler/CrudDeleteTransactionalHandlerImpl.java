package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.exception.CrudDeleteException;
import dev.krud.crudframework.crud.hooks.delete.CRUDOnDeleteHook;
import dev.krud.crudframework.crud.policy.PolicyRuleType;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CrudDeleteTransactionalHandlerImpl implements CrudDeleteTransactionalHandler {
    private final CrudHelper crudHelper;

    private final CrudSecurityHandler crudSecurityHandler;

    public CrudDeleteTransactionalHandlerImpl(CrudHelper crudHelper, CrudSecurityHandler crudSecurityHandler) {
        this.crudHelper = crudHelper;
        this.crudSecurityHandler = crudSecurityHandler;
    }

    @Override
    @Transactional(readOnly = false)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity deleteHardTransactional(DynamicModelFilter filter, Class<Entity> clazz, List<CRUDOnDeleteHook<ID, Entity>> onHooks, boolean applyPolicies) {
        Entity entity = getEntityForDeletion(filter, clazz, applyPolicies);

        for(CRUDOnDeleteHook<ID, Entity> onHook : onHooks) {
            onHook.run(entity);
        }

        crudHelper.getCrudDaoForEntity(clazz).hardDeleteById(entity.getId(), clazz);
        return entity;
    }

    @Override
    @Transactional(readOnly = false)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity deleteSoftTransactional(DynamicModelFilter filter, Field deleteField, Class<Entity> clazz, List<CRUDOnDeleteHook<ID, Entity>> onHooks, boolean applyPolicies) {
        Entity entity = getEntityForDeletion(filter, clazz, applyPolicies);

        for(CRUDOnDeleteHook<ID, Entity> onHook : onHooks) {
            onHook.run(entity);
        }
        try {
            deleteField.setAccessible(true);
            deleteField.set(entity, true);
        } catch	(IllegalAccessException e) {
            CrudDeleteException exception = new CrudDeleteException("Error deleting entity " + clazz.getName());
            exception.initCause(e);
            throw exception;
        }

        return entity;
    }

    @Override
    @Transactional(readOnly = false)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> bulkDeleteHardTransactional(DynamicModelFilter filter, Class<Entity> entityClazz, List<CRUDOnDeleteHook<ID, Entity>> onHooks, boolean applyPolicies) {
        List<Entity> entities = getEntitiesForDeletion(filter, entityClazz, applyPolicies);

        List<Entity> result = new ArrayList<>();
        for(Entity entity : entities) {
            result.add(deleteHardTransactional(filter, entityClazz, onHooks, applyPolicies));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> bulkDeleteSoftTransactional(DynamicModelFilter filter, Field deleteField, Class<Entity> entityClazz, List<CRUDOnDeleteHook<ID, Entity>> onHooks, boolean applyPolicies) {
        List<Entity> entities = getEntitiesForDeletion(filter, entityClazz, applyPolicies);

        List<Entity> result = new ArrayList<>();
        for(Entity entity : entities) {
            result.add(deleteSoftTransactional(filter, deleteField, entityClazz, onHooks, applyPolicies));
        }

        return result;
    }

    private <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity getEntityForDeletion(DynamicModelFilter filter, Class<Entity> clazz, boolean applyPolicies) {
        Entity entity = crudHelper.getEntity(filter, clazz, null);

        if(crudHelper.isEntityDeleted(entity)) {
            throw new CrudDeleteException("Entity of type [ " + clazz.getSimpleName() + " ] does not exist or cannot be deleted");
        }

        if (applyPolicies) {
            crudSecurityHandler.evaluatePostRulesAndThrow(entity, PolicyRuleType.CAN_DELETE, clazz);
        }
        return entity;
    }

    private <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> getEntitiesForDeletion(DynamicModelFilter filter, Class<Entity> clazz, boolean applyPolicies) {
        List<Entity> entities = crudHelper.getEntities(filter, clazz, null);

        for(Entity entity : entities) {
            if(crudHelper.isEntityDeleted(entity)) {
                throw new CrudDeleteException("Entity of type [ " + clazz.getSimpleName() + " ] does not exist or cannot be deleted");
            }

            if (applyPolicies) {
                crudSecurityHandler.evaluatePostRulesAndThrow(entity, PolicyRuleType.CAN_DELETE, clazz);
            }
        }

        return entities;
    }
}
