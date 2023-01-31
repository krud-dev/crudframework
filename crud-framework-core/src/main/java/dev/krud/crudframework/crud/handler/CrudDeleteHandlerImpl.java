package dev.krud.crudframework.crud.handler;

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
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

@WrapException(CrudDeleteException.class)
public class CrudDeleteHandlerImpl implements CrudDeleteHandler {

	@Autowired
	private CrudHelper crudHelper;

	@Autowired
	private CrudDeleteTransactionalHandler crudDeleteTransactionalHandler;

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
			entity = crudDeleteTransactionalHandler.deleteHardTransactional(filter, clazz, hooks.getOnHooks(), applyPolicies);
		} else {
			entity = crudDeleteTransactionalHandler.deleteSoftTransactional(filter, metadataDTO.getDeleteField(), clazz, hooks.getOnHooks(), applyPolicies);
		}

		crudHelper.evictEntityFromCache(entity);

		for(CRUDPostDeleteHook<ID, Entity> postHook : hooks.getPostHooks()) {
			postHook.run(entity);
		}
	}
}
