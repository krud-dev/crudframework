package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.exception.CrudDeleteException;
import dev.krud.crudframework.crud.hooks.create.CRUDOnCreateHook;
import dev.krud.crudframework.crud.hooks.create.from.CRUDOnCreateFromHook;
import dev.krud.crudframework.model.BaseCrudEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public class CrudCreateTransactionalHandlerImpl implements CrudCreateTransactionalHandler {
    private final CrudHelper crudHelper;

    private final CrudSecurityHandler crudSecurityHandler;

    public CrudCreateTransactionalHandlerImpl(CrudHelper crudHelper, CrudSecurityHandler crudSecurityHandler) {
        this.crudHelper = crudHelper;
        this.crudSecurityHandler = crudSecurityHandler;
    }

    @Override
    @Transactional(readOnly = false)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity createTransactional(Entity entity, List<CRUDOnCreateHook<ID, Entity>> onHooks) {

        for(CRUDOnCreateHook<ID, Entity> onHook : onHooks) {
            onHook.run(entity);
        }

        crudHelper.validate(entity);

        return crudHelper.getCrudDaoForEntity(entity.getClass()).saveOrUpdate(entity);
    }
    @Override
    @Transactional(readOnly = false)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity createFromTransactional(Object object, Class<Entity> clazz, List<CRUDOnCreateFromHook<ID, Entity>> onHooks) {
        Entity entity = crudHelper.fill(object, clazz);

        if(entity.exists()) {
            throw new CrudDeleteException("Entity of type [ " + clazz.getSimpleName() + " ] with ID [ " + entity.getId() + " ] already exists and cannot be created");
        }

        for(CRUDOnCreateFromHook<ID, Entity> onHook : onHooks) {
            onHook.run(entity, object);
        }

        crudHelper.validate(entity);

        return crudHelper.getCrudDaoForEntity(clazz).saveOrUpdate(entity);
    }
}
