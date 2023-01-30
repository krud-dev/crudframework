package dev.krud.crudframework.crud.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import dev.krud.crudframework.crud.exception.CrudUpdateException;
import dev.krud.crudframework.crud.hooks.HooksDTO;
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

import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@WrapException(value = CrudUpdateException.class)
public class CrudUpdateHandlerImpl implements CrudUpdateHandler {

	@Autowired
	private CrudHelper crudHelper;

	@Resource(name = "crudUpdateHandler")
	private CrudUpdateHandler crudUpdateHandlerProxy;

	@Autowired
	private CrudSecurityHandler crudSecurityHandler;

	@Override
	@Transactional(readOnly = false)
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> updateManyTransactional(List<Entity> entities,
																											 HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> hooks, Boolean persistCopy, boolean applyPolicies) {
		List<Entity> finalEntityList = new ArrayList<>();
		for(Entity entity : entities) {
			finalEntityList.add(crudUpdateHandlerProxy.updateInternal(entity, hooks, applyPolicies));
		}

		return finalEntityList;
	}

	@Override
	@Transactional(readOnly = false)
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> updateByFilterTransactional(DynamicModelFilter filter, Class<Entity> entityClazz,
																												 HooksDTO<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>> hooks, Boolean persistCopy, boolean applyPolicies) {
		List<Entity> entities = crudHelper.getEntities(filter, entityClazz, persistCopy);
		return crudUpdateHandlerProxy.updateManyTransactional(entities, hooks, persistCopy, applyPolicies);
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

		entity = crudUpdateHandlerProxy.updateTransactional(entity, filter, hooks.getOnHooks(), applyPolicies);

		crudHelper.evictEntityFromCache(entity);

		for(CRUDPostUpdateHook<ID, Entity> postHook : hooks.getPostHooks()) {
			postHook.run(entity);
		}

		return entity;
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

		for(CRUDOnUpdateHook<ID, Entity> onHook : onHooks) {
			onHook.run(entity);
		}

		crudHelper.validate(entity);

		return crudHelper.getCrudDaoForEntity(entity.getClass()).saveOrUpdate(entity);
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

		crudHelper.validate(object);

		Entity entity = crudUpdateHandlerProxy.updateFromTransactional(filter, object, clazz, hooks.getOnHooks(), applyPolicies);

		crudHelper.evictEntityFromCache(entity);

		for(CRUDPostUpdateFromHook<ID, Entity> postHook : hooks.getPostHooks()) {
			postHook.run(entity);
		}

		return entity;
	}

	@Override
	@Transactional(readOnly = false)
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity updateFromTransactional(DynamicModelFilter filter, Object object, Class<Entity> clazz, List<CRUDOnUpdateFromHook<ID, Entity>> onHooks, boolean applyPolicies) {
		Entity entity = crudHelper.getEntity(filter, clazz, null);

		if(entity == null) {
			throw new CrudUpdateException("Entity of type [ " + clazz.getSimpleName() + " ] does not exist or cannot be updated");
		}

		if (applyPolicies) {
			crudSecurityHandler.evaluatePostRulesAndThrow(entity, PolicyRuleType.CAN_UPDATE, clazz);
		}

		crudHelper.fill(object, entity);

		for(CRUDOnUpdateFromHook<ID, Entity> onHook : onHooks) {
			onHook.run(entity, object);
		}

		crudHelper.validate(entity);

		return crudHelper.getCrudDaoForEntity(clazz).saveOrUpdate(entity);
	}


}
