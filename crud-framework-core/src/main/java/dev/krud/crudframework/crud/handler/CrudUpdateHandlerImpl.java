package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.exception.CrudUpdateException;
import dev.krud.crudframework.crud.hooks.HooksDTO;
import dev.krud.crudframework.crud.hooks.interfaces.FieldChangeHook;
import dev.krud.crudframework.crud.hooks.interfaces.UpdateFromHooks;
import dev.krud.crudframework.crud.hooks.interfaces.UpdateHooks;
import dev.krud.crudframework.crud.hooks.update.CRUDOnUpdateHook;
import dev.krud.crudframework.crud.hooks.update.CRUDPostUpdateHook;
import dev.krud.crudframework.crud.hooks.update.CRUDPreUpdateHook;
import dev.krud.crudframework.crud.hooks.update.from.CRUDOnUpdateFromHook;
import dev.krud.crudframework.crud.hooks.update.from.CRUDPostUpdateFromHook;
import dev.krud.crudframework.crud.hooks.update.from.CRUDPreUpdateFromHook;
import dev.krud.crudframework.crud.policy.PolicyRuleType;
import dev.krud.crudframework.exception.WrapException;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import dev.krud.crudframework.modelfilter.FilterFields;
import dev.krud.crudframework.modelfilter.enums.FilterFieldDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@WrapException(value = CrudUpdateException.class)
public class CrudUpdateHandlerImpl implements CrudUpdateHandler {

	@Autowired
	private CrudHelper crudHelper;

	@Autowired
	private CrudUpdateTransactionalHandler crudUpdateTransactionalHandler;

	@Autowired
	private CrudSecurityHandler crudSecurityHandler;

	@Override
	@Transactional(readOnly = false)
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> updateMany(List<Entity> entities,
																								HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> hooks, Boolean persistCopy, boolean applyPolicies) {
		List<Entity> finalEntityList = new ArrayList<>();
		for(Entity entity : entities) {
			finalEntityList.add(updateInternal(entity, hooks, applyPolicies));
		}

		return finalEntityList;
	}

	@Override
	@Transactional(readOnly = false)
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> updateByFilter(DynamicModelFilter filter, Class<Entity> entityClazz,
																									HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> hooks, Boolean persistCopy, boolean applyPolicies) {
		List<Entity> entities = crudHelper.getEntities(filter, entityClazz, persistCopy);
		return updateMany(entities, hooks, persistCopy, applyPolicies);
	}

	@Override
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateInternal(Entity entity, HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> hooks, boolean applyPolicies) {
		Objects.requireNonNull(entity, "Entity cannot be null");
		Objects.requireNonNull(entity.getId(), "Entity ID cannot be null");
		if (!entity.exists()) {
			throw new CrudUpdateException("Entity of type [ " + entity.getClass().getSimpleName() + " ] does not exist or cannot be updated");
		}

		DynamicModelFilter filter = new DynamicModelFilter()
				.add(FilterFields.eq("id", FilterFieldDataType.get(entity.getId().getClass()), entity.getId()));

		if (applyPolicies) {
			crudSecurityHandler.evaluatePreRulesAndThrow(PolicyRuleType.CAN_UPDATE, entity.getClass());
			crudSecurityHandler.decorateFilter(entity.getClass(), filter);
		}

		crudHelper.checkEntityImmutability(entity.getClass());

		List<UpdateHooks> updateHooksList = crudHelper.getHooks(UpdateHooks.class, entity.getClass());

		if(updateHooksList != null && !updateHooksList.isEmpty()) {
			for(UpdateHooks<ID, Entity> updateHooks : updateHooksList) {
				hooks.getPreHooks().add(0, updateHooks::preUpdate);
				hooks.getOnHooks().add(0, updateHooks::onUpdate);
				hooks.getPostHooks().add(0, updateHooks::postUpdate);
			}
		}

		for(CRUDPreUpdateHook<ID, Entity> preHook : hooks.getPreHooks()) {
			preHook.run(entity);
		}

        List<FieldChangeHook> fieldChangeHooks = crudHelper.getFieldChangeHooks(entity.getClass());
        Entity original = null;
        if (fieldChangeHooks != null && !fieldChangeHooks.isEmpty()) {
            original = (Entity) entity.saveOrGetCopy();
        }

        for (FieldChangeHook fieldChangeHook : fieldChangeHooks) {
            fieldChangeHook.runPreChange(entity, original);
        }

		entity = crudUpdateTransactionalHandler.updateTransactional(entity, filter, hooks.getOnHooks(), fieldChangeHooks, applyPolicies);

		crudHelper.evictEntityFromCache(entity);

		for(CRUDPostUpdateHook<ID, Entity> postHook : hooks.getPostHooks()) {
			postHook.run(entity);
		}

        for (FieldChangeHook fieldChangeHook : fieldChangeHooks) {
            fieldChangeHook.runPostChange(entity, original);
        }

		return entity;
	}

	@Override
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateFromInternal(ID id, Object object, Class<Entity> clazz,
																								  HooksDTO<CRUDPreUpdateFromHook<ID, Entity>, CRUDOnUpdateFromHook<ID, Entity>, CRUDPostUpdateFromHook<ID, Entity>> hooks, boolean applyPolicies) {
		DynamicModelFilter filter = new DynamicModelFilter()
				.add(FilterFields.eq("id", FilterFieldDataType.get(id.getClass()), id));
		if (applyPolicies) {
			crudSecurityHandler.evaluatePreRulesAndThrow(PolicyRuleType.CAN_UPDATE, clazz);
			crudSecurityHandler.decorateFilter(clazz, filter);
		}
		crudHelper.checkEntityImmutability(clazz);

		List<UpdateFromHooks> updateFromHooksList = crudHelper.getHooks(UpdateFromHooks.class, clazz);

		if(updateFromHooksList != null && !updateFromHooksList.isEmpty()) {
			for(UpdateFromHooks<ID, Entity> updateFromHooks : updateFromHooksList) {
				hooks.getPreHooks().add(0, updateFromHooks::preUpdateFrom);
				hooks.getOnHooks().add(0, updateFromHooks::onUpdateFrom);
				hooks.getPostHooks().add(0, updateFromHooks::postUpdateFrom);
			}
		}

		Objects.requireNonNull(object, "Object cannot be null");
		for(CRUDPreUpdateFromHook<ID, Entity> preHook : hooks.getPreHooks()) {
			preHook.run(id, object);
		}

        List<FieldChangeHook> fieldChangeHooks = crudHelper.getFieldChangeHooks(clazz);

		Entity entity = crudUpdateTransactionalHandler.updateFromTransactional(filter, object, clazz, hooks.getOnHooks(), fieldChangeHooks, applyPolicies);

		crudHelper.evictEntityFromCache(entity);

		for(CRUDPostUpdateFromHook<ID, Entity> postHook : hooks.getPostHooks()) {
			postHook.run(entity);
		}
        for (FieldChangeHook fieldChangeHook : fieldChangeHooks) {
            fieldChangeHook.runPostChange(entity, entity.saveOrGetCopy());
        }

		return entity;
	}

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> bulkUpdate(List<Entity> entities, HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> hooks, boolean applyPolicies) {
        if (entities.isEmpty()) {
            throw new IllegalStateException("Cannot update an empty list of entities");
        }

        Class<Entity> entityClazz = (Class<Entity>) entities.get(0).getClass();
        List<FieldChangeHook> fieldChangeHooks = crudHelper.getFieldChangeHooks(entityClazz);
        DynamicModelFilter filter = new DynamicModelFilter()
                .add(FilterFields.in("id", FilterFieldDataType.get(entities.get(0).getId().getClass()), entities.stream().map(BaseCrudEntity::getId).toArray()));

        if (applyPolicies) {
            crudSecurityHandler.evaluatePreRulesAndThrow(PolicyRuleType.CAN_UPDATE, entityClazz);
            crudSecurityHandler.decorateFilter(entityClazz, filter);
        }

        crudHelper.checkEntityImmutability(entityClazz);

        List<UpdateHooks> updateHooksList = crudHelper.getHooks(UpdateHooks.class, entityClazz);

        if (updateHooksList != null && !updateHooksList.isEmpty()) {
            for (UpdateHooks<ID, Entity> updateHooks : updateHooksList) {
                hooks.getPreHooks().add(0, updateHooks::preUpdate);
                hooks.getOnHooks().add(0, updateHooks::onUpdate);
                hooks.getPostHooks().add(0, updateHooks::postUpdate);
            }
        }

        Map<ID, Entity> originals = new HashMap<>();

        for (Entity entity : entities) {
            Objects.requireNonNull(entity, "Entity cannot be null");
            Objects.requireNonNull(entity.getId(), "Entity ID cannot be null");
            if (!entity.exists()) {
                throw new CrudUpdateException("Entity of type [ " + entity.getClass().getSimpleName() + " ] does not exist or cannot be updated");
            }

            for (CRUDPreUpdateHook<ID, Entity> preHook : hooks.getPreHooks()) {
                preHook.run(entity);
            }

            if (fieldChangeHooks != null && !fieldChangeHooks.isEmpty()) {
                originals.put(entity.getId(), (Entity) entity.saveOrGetCopy());
            }

            for (FieldChangeHook fieldChangeHook : fieldChangeHooks) {
                fieldChangeHook.runPreChange(entity, originals.get(entity.getId()));
            }
        }

        List<Entity> result = crudUpdateTransactionalHandler.bulkUpdateTransactional(entities, filter, hooks.getOnHooks(), fieldChangeHooks, applyPolicies);

        for (Entity entity : result) {
            crudHelper.evictEntityFromCache(entity);

            for (CRUDPostUpdateHook<ID, Entity> postHook : hooks.getPostHooks()) {
                postHook.run(entity);
            }

            for (FieldChangeHook fieldChangeHook : fieldChangeHooks) {
                fieldChangeHook.runPostChange(entity, originals.get(entity.getId()));
            }
        }

        return result;
    }
}
