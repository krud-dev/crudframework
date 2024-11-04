package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.exception.CrudCreateException;
import dev.krud.crudframework.crud.hooks.HooksDTO;
import dev.krud.crudframework.crud.hooks.create.CRUDOnCreateHook;
import dev.krud.crudframework.crud.hooks.create.CRUDPostCreateHook;
import dev.krud.crudframework.crud.hooks.create.CRUDPreCreateHook;
import dev.krud.crudframework.crud.hooks.create.from.CRUDOnCreateFromHook;
import dev.krud.crudframework.crud.hooks.create.from.CRUDPostCreateFromHook;
import dev.krud.crudframework.crud.hooks.create.from.CRUDPreCreateFromHook;
import dev.krud.crudframework.crud.hooks.interfaces.CreateFromHooks;
import dev.krud.crudframework.crud.hooks.interfaces.CreateHooks;
import dev.krud.crudframework.crud.hooks.interfaces.FieldChangeHook;
import dev.krud.crudframework.crud.policy.PolicyRuleType;
import dev.krud.crudframework.exception.WrapException;
import dev.krud.crudframework.model.BaseCrudEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@WrapException(CrudCreateException.class)
public class CrudCreateHandlerImpl implements CrudCreateHandler {

    @Autowired
    private CrudHelper crudHelper;

    @Autowired
    private CrudCreateTransactionalHandler crudCreateTransactionalHandler;

    @Autowired
    private CrudSecurityHandler crudSecurityHandler;

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity createInternal(Entity entity, HooksDTO<CRUDPreCreateHook<ID, Entity>, CRUDOnCreateHook<ID, Entity>, CRUDPostCreateHook<ID, Entity>> hooks, boolean applyPolicies) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        if (applyPolicies) {
            crudSecurityHandler.evaluatePreRulesAndThrow(PolicyRuleType.CAN_CREATE, entity.getClass());
        }

        List<CreateHooks> createHooksList = crudHelper.getHooks(CreateHooks.class, entity.getClass());

        if (createHooksList != null && !createHooksList.isEmpty()) {
            for (CreateHooks<ID, Entity> createHooks : createHooksList) {
                hooks.getPreHooks().add(0, createHooks::preCreate);
                hooks.getOnHooks().add(0, createHooks::onCreate);
                hooks.getPostHooks().add(0, createHooks::postCreate);
            }
        }

        for (CRUDPreCreateHook<ID, Entity> preHook : hooks.getPreHooks()) {
            preHook.run(entity);
        }

        List<FieldChangeHook> fieldChangeHooks = crudHelper.getFieldChangeHooks(entity.getClass());
        for (FieldChangeHook fieldChangeHook : fieldChangeHooks) {
            fieldChangeHook.runPreChange(entity, entity.generateEmptyEntity());
        }

        entity = crudCreateTransactionalHandler.createTransactional(entity, hooks.getOnHooks(), fieldChangeHooks);
        for (CRUDPostCreateHook<ID, Entity> postHook : hooks.getPostHooks()) {
            postHook.run(entity);
        }
        for (FieldChangeHook fieldChangeHook : fieldChangeHooks) {
            fieldChangeHook.runPostChange(entity, entity.generateEmptyEntity());
        }

        return entity;
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> bulkCreateInternal(List<Entity> entities, boolean applyPolicies) {
        Objects.requireNonNull(entities, "Entity cannot be null");
        if (entities.isEmpty()) {
            throw new IllegalStateException("Cannot create an empty list of entities");
        }

        Class<Entity> entityClazz = (Class<Entity>) entities.get(0).getClass();

        if (applyPolicies) {
            crudSecurityHandler.evaluatePreRulesAndThrow(PolicyRuleType.CAN_CREATE, entityClazz);
        }

        List<CreateHooks> hooks = crudHelper.getHooks(CreateHooks.class, entityClazz);
        hooks.forEach(hook -> entities.forEach(hook::preCreate));

        List<FieldChangeHook> fieldChangeHooks = crudHelper.getFieldChangeHooks(entityClazz);
        fieldChangeHooks.forEach(hook -> entities.forEach(entity -> hook.runPreChange(entity, entity.generateEmptyEntity())));

        List<Entity> createdEntities = crudCreateTransactionalHandler.bulkCreateTransactional(entities, hooks, fieldChangeHooks);
        hooks.forEach(hook -> createdEntities.forEach(hook::postCreate));
        fieldChangeHooks.forEach(hook -> createdEntities.forEach(entity -> hook.runPostChange(entity, entity.generateEmptyEntity())));
        return createdEntities;
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity createFromInternal(Object object, Class<Entity> clazz,
                                                                                                  HooksDTO<CRUDPreCreateFromHook<ID, Entity>, CRUDOnCreateFromHook<ID, Entity>, CRUDPostCreateFromHook<ID, Entity>> hooks) {
        Objects.requireNonNull(object, "Object cannot be null");

        List<CreateFromHooks> createFromHooksList = crudHelper.getHooks(CreateFromHooks.class, clazz);

        if (createFromHooksList != null && !createFromHooksList.isEmpty()) {
            for (CreateFromHooks<ID, Entity> createFromHooks : createFromHooksList) {
                hooks.getPreHooks().add(0, createFromHooks::preCreateFrom);
                hooks.getOnHooks().add(0, createFromHooks::onCreateFrom);
                hooks.getPostHooks().add(0, createFromHooks::postCreateFrom);
            }
        }

        for (CRUDPreCreateFromHook preHook : hooks.getPreHooks()) {
            preHook.run(object);
        }

        List<FieldChangeHook> fieldChangeHooks = crudHelper.getFieldChangeHooks(clazz);

        Entity entity = crudCreateTransactionalHandler.createFromTransactional(object, clazz, hooks.getOnHooks(), fieldChangeHooks);
        for (CRUDPostCreateFromHook<ID, Entity> postHook : hooks.getPostHooks()) {
            postHook.run(entity);
        }
        for (FieldChangeHook fieldChangeHook : fieldChangeHooks) {
            fieldChangeHook.runPostChange(entity, entity.generateEmptyEntity());
        }

        return entity;
    }

}
