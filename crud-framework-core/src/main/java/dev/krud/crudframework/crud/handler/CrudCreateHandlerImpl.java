package dev.krud.crudframework.crud.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import dev.krud.crudframework.crud.exception.CrudCreateException;
import dev.krud.crudframework.crud.exception.CrudDeleteException;
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

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@WrapException(CrudCreateException.class)
public class CrudCreateHandlerImpl implements CrudCreateHandler {

	@Autowired
	private CrudHelper crudHelper;

	@Resource(name = "crudCreateHandler")
	private CrudCreateHandler crudCreateHandlerProxy;

	@Autowired
	private CrudSecurityHandler crudSecurityHandler;

	@Override
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity createInternal(Entity entity, HooksDTO<CRUDPreCreateHook<ID, Entity>, CRUDOnCreateHook<ID, Entity>, CRUDPostCreateHook<ID, Entity>> hooks, boolean applyPolicies) {
		Objects.requireNonNull(entity, "Entity cannot be null");
		if(applyPolicies) {
			crudSecurityHandler.evaluatePreRulesAndThrow(PolicyRuleType.CAN_CREATE, entity.getClass());
		}

		List<CreateHooks> createHooksList = crudHelper.getHooks(CreateHooks.class, entity.getClass());

		if(createHooksList != null && !createHooksList.isEmpty()) {
			for(CreateHooks<ID, Entity> createHooks : createHooksList) {
				hooks.getPreHooks().add(0, createHooks::preCreate);
				hooks.getOnHooks().add(0, createHooks::onCreate);
				hooks.getPostHooks().add(0, createHooks::postCreate);
			}
		}

		for(CRUDPreCreateHook<ID, Entity> preHook : hooks.getPreHooks()) {
			preHook.run(entity);
		}

			entity = crudCreateHandlerProxy.createTransactional(entity, hooks.getOnHooks());
		for(CRUDPostCreateHook<ID, Entity> postHook : hooks.getPostHooks()) {
			postHook.run(entity);
		}

		return entity;
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
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity createFromInternal(Object object, Class<Entity> clazz,
			HooksDTO<CRUDPreCreateFromHook<ID, Entity>, CRUDOnCreateFromHook<ID, Entity>, CRUDPostCreateFromHook<ID, Entity>> hooks) {
		Objects.requireNonNull(object, "Object cannot be null");

		List<CreateFromHooks> createFromHooksList = crudHelper.getHooks(CreateFromHooks.class, clazz);

		if(createFromHooksList != null && !createFromHooksList.isEmpty()) {
			for(CreateFromHooks<ID, Entity> createFromHooks : createFromHooksList) {
				hooks.getPreHooks().add(0, createFromHooks::preCreateFrom);
				hooks.getOnHooks().add(0, createFromHooks::onCreateFrom);
				hooks.getPostHooks().add(0, createFromHooks::postCreateFrom);
			}
		}

		for(CRUDPreCreateFromHook preHook : hooks.getPreHooks()) {
			preHook.run(object);
		}

		crudHelper.validate(object);

		Entity entity = crudCreateHandlerProxy.createFromTransactional(object, clazz, hooks.getOnHooks());
		for(CRUDPostCreateFromHook<ID, Entity> postHook : hooks.getPostHooks()) {
			postHook.run(entity);
		}
		return entity;
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
