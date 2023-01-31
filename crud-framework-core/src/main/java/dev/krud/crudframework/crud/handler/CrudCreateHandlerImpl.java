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
import dev.krud.crudframework.crud.policy.PolicyRuleType;
import dev.krud.crudframework.exception.WrapException;
import dev.krud.crudframework.model.BaseCrudEntity;
import jakarta.annotation.Resource;
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

        entity = crudCreateTransactionalHandler.createTransactional(entity, hooks.getOnHooks());
        for (CRUDPostCreateHook<ID, Entity> postHook : hooks.getPostHooks()) {
            postHook.run(entity);
        }

        return entity;
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

        crudHelper.validate(object);

        Entity entity = crudCreateTransactionalHandler.createFromTransactional(object, clazz, hooks.getOnHooks());
        for (CRUDPostCreateFromHook<ID, Entity> postHook : hooks.getPostHooks()) {
            postHook.run(entity);
        }
        return entity;
    }

}
