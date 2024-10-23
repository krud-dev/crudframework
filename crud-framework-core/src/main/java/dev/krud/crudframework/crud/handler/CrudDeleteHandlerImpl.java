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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void bulkDelete(List<ID> ids, Class<Entity> entityClazz, HooksDTO<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>> hooks, boolean applyPolicies) {

        Class<ID> idClass = (Class<ID>) ids.get(0).getClass();

        DynamicModelFilter filter = new DynamicModelFilter()
                .add(FilterFields.in("id", FilterFieldDataType.get(idClass), ids.stream().map(id -> (ID) id).collect(Collectors.toList())));

        if (applyPolicies) {
            crudSecurityHandler.evaluatePreRulesAndThrow(PolicyRuleType.CAN_DELETE, entityClazz);
            crudSecurityHandler.decorateFilter(entityClazz, filter);
        }

        crudHelper.checkEntityImmutability(entityClazz);
        crudHelper.checkEntityDeletability(entityClazz);

        List<DeleteHooks> deleteHooksList = crudHelper.getHooks(DeleteHooks.class, entityClazz);

        if(deleteHooksList != null && !deleteHooksList.isEmpty()) {
            for(DeleteHooks<ID, Entity> deleteHooks : deleteHooksList) {
                hooks.getPreHooks().add(0, deleteHooks::preDelete);
                hooks.getOnHooks().add(0, deleteHooks::onDelete);
                hooks.getPostHooks().add(0, deleteHooks::postDelete);
            }
        }

        EntityMetadataDTO metadataDTO = crudHelper.getEntityMetadata(entityClazz);

        for (ID id : ids) {
            for (CRUDPreDeleteHook<ID, Entity> preHook : hooks.getPreHooks()) {
                preHook.run(id);
            }
        }

        List<Entity> result;

        if(metadataDTO.getDeleteableType() == EntityMetadataDTO.DeleteableType.Hard) {
            result = crudDeleteTransactionalHandler.bulkDeleteHardTransactional(filter, entityClazz, hooks.getOnHooks(), applyPolicies);
        } else {
            result = crudDeleteTransactionalHandler.bulkDeleteSoftTransactional(filter, metadataDTO.getDeleteField(), entityClazz, hooks.getOnHooks(), applyPolicies);
        }

        for (Entity entity : result) {
            crudHelper.evictEntityFromCache(entity);
            for (CRUDPostDeleteHook<ID, Entity> postHook : hooks.getPostHooks()) {
                postHook.run(entity);
            }
        }
    }
}
