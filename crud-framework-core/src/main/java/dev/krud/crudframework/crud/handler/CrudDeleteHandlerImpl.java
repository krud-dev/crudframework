package dev.krud.crudframework.crud.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import dev.krud.crudframework.crud.exception.CrudDeleteException;
import dev.krud.crudframework.crud.hooks.HooksDTO;
import dev.krud.crudframework.crud.hooks.delete.CRUDOnDeleteHook;
import dev.krud.crudframework.crud.hooks.delete.CRUDPostDeleteHook;
import dev.krud.crudframework.crud.hooks.delete.CRUDPreDeleteHook;
import dev.krud.crudframework.crud.hooks.interfaces.DeleteHooks;
import dev.krud.crudframework.crud.model.EntityMetadataDTO;
import dev.krud.crudframework.crud.policy.PolicyRuleType;
import dev.krud.crudframework.exception.WrapException;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import dev.krud.crudframework.modelfilter.FilterFields;
import dev.krud.crudframework.modelfilter.enums.FilterFieldDataType;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@WrapException(CrudDeleteException.class)
public class CrudDeleteHandlerImpl implements CrudDeleteHandler {

	@Autowired
	private CrudHelper crudHelper;

	@Resource(name = "crudDeleteHandler")
	private CrudDeleteHandler crudDeleteHandlerProxy;

	@Autowired
	private CrudSecurityHandler crudSecurityHandler;

	@Override
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void deleteInternal(ID id, Class<Entity> clazz,
																							HooksDTO<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>> hooks, boolean applyPolicies) {
		DynamicModelFilter filter = new DynamicModelFilter()
				.add(FilterFields.eq("id", FilterFieldDataType.get(id.getClass()), id));

		if (applyPolicies) {
			crudSecurityHandler.evaluatePreRulesAndThrow(PolicyRuleType.CAN_DELETE, clazz);
			crudSecurityHandler.decorateFilter(clazz, filter);
		}

		crudHelper.checkEntityImmutability(clazz);
		crudHelper.checkEntityDeletability(clazz);

		List<DeleteHooks> deleteHooksList = crudHelper.getHooks(DeleteHooks.class, clazz);

		if(deleteHooksList != null && !deleteHooksList.isEmpty()) {
			for(DeleteHooks<ID, Entity> deleteHooks : deleteHooksList) {
				hooks.getPreHooks().add(0, deleteHooks::preDelete);
				hooks.getOnHooks().add(0, deleteHooks::onDelete);
				hooks.getPostHooks().add(0, deleteHooks::postDelete);
			}
		}

		for(CRUDPreDeleteHook<ID, Entity> preHook : hooks.getPreHooks()) {
			preHook.run(id);
		}

		EntityMetadataDTO metadataDTO = crudHelper.getEntityMetadata(clazz);

		Entity entity;

		if(metadataDTO.getDeleteableType() == EntityMetadataDTO.DeleteableType.Hard) {
			entity = crudDeleteHandlerProxy.deleteHardTransactional(filter, clazz, hooks.getOnHooks(), applyPolicies);
		} else {
			entity = crudDeleteHandlerProxy.deleteSoftTransactional(filter, metadataDTO.getDeleteField(), clazz, hooks.getOnHooks(), applyPolicies);
		}

		crudHelper.evictEntityFromCache(entity);

		for(CRUDPostDeleteHook<ID, Entity> postHook : hooks.getPostHooks()) {
			postHook.run(entity);
		}
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

}
