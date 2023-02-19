package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.crud.exception.CrudException;
import dev.krud.crudframework.crud.hooks.create.CRUDOnCreateHook;
import dev.krud.crudframework.crud.hooks.create.CRUDPostCreateHook;
import dev.krud.crudframework.crud.hooks.create.CRUDPreCreateHook;
import dev.krud.crudframework.crud.hooks.create.from.CRUDOnCreateFromHook;
import dev.krud.crudframework.crud.hooks.create.from.CRUDPostCreateFromHook;
import dev.krud.crudframework.crud.hooks.create.from.CRUDPreCreateFromHook;
import dev.krud.crudframework.crud.hooks.delete.CRUDOnDeleteHook;
import dev.krud.crudframework.crud.hooks.delete.CRUDPostDeleteHook;
import dev.krud.crudframework.crud.hooks.delete.CRUDPreDeleteHook;
import dev.krud.crudframework.crud.hooks.index.CRUDOnIndexHook;
import dev.krud.crudframework.crud.hooks.index.CRUDPostIndexHook;
import dev.krud.crudframework.crud.hooks.index.CRUDPreIndexHook;
import dev.krud.crudframework.crud.hooks.show.CRUDOnShowHook;
import dev.krud.crudframework.crud.hooks.show.CRUDPostShowHook;
import dev.krud.crudframework.crud.hooks.show.CRUDPreShowHook;
import dev.krud.crudframework.crud.hooks.show.by.CRUDOnShowByHook;
import dev.krud.crudframework.crud.hooks.show.by.CRUDPostShowByHook;
import dev.krud.crudframework.crud.hooks.show.by.CRUDPreShowByHook;
import dev.krud.crudframework.crud.hooks.update.CRUDOnUpdateHook;
import dev.krud.crudframework.crud.hooks.update.CRUDPostUpdateHook;
import dev.krud.crudframework.crud.hooks.update.CRUDPreUpdateHook;
import dev.krud.crudframework.crud.hooks.update.from.CRUDOnUpdateFromHook;
import dev.krud.crudframework.crud.hooks.update.from.CRUDPostUpdateFromHook;
import dev.krud.crudframework.crud.hooks.update.from.CRUDPreUpdateFromHook;
import dev.krud.crudframework.crud.model.MassUpdateCRUDRequestBuilder;
import dev.krud.crudframework.crud.model.ReadCRUDRequestBuilder;
import dev.krud.crudframework.crud.model.UpdateCRUDRequestBuilder;
import dev.krud.crudframework.exception.WrapException;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import dev.krud.crudframework.ro.PagedResult;
import dev.krud.crudframework.util.DynamicModelFilterUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@WrapException(CrudException.class)
public class CrudHandlerImpl implements CrudHandler {
    private CrudReadHandler crudReadHandler;

    private CrudUpdateHandler crudUpdateHandler;

    private CrudDeleteHandler crudDeleteHandler;

    private CrudCreateHandler crudCreateHandler;

    @Autowired
    public void setCrudReadHandler(CrudReadHandler crudReadHandler) {
        this.crudReadHandler = crudReadHandler;
    }

    @Autowired
    public void setCrudUpdateHandler(CrudUpdateHandler crudUpdateHandler) {
        this.crudUpdateHandler = crudUpdateHandler;
    }

    @Autowired
    public void setCrudDeleteHandler(CrudDeleteHandler crudDeleteHandler) {
        this.crudDeleteHandler = crudDeleteHandler;
    }

    @Autowired
    public void setCrudCreateHandler(CrudCreateHandler crudCreateHandler) {
        this.crudCreateHandler = crudCreateHandler;
    }

    @Autowired
    public void setCrudHelper(CrudHelper crudHelper) {
        this.crudHelper = crudHelper;
    }

    @Autowired
    private CrudHelper crudHelper;

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> ReadCRUDRequestBuilder<CRUDPreIndexHook<ID, Entity>, CRUDOnIndexHook<ID, Entity>, CRUDPostIndexHook<ID, Entity>, PagedResult<Entity>> index(
            DynamicModelFilter filter, Class<Entity> clazz) {
        return new ReadCRUDRequestBuilder<>(
                (context) -> crudReadHandler.indexInternal(filter, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies(), false),
                (context) -> crudReadHandler.indexInternal(filter, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies(), true).getTotal()
        );
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, RO> ReadCRUDRequestBuilder<CRUDPreIndexHook<ID, Entity>, CRUDOnIndexHook<ID, Entity>, CRUDPostIndexHook<ID, Entity>, PagedResult<RO>> index(
            DynamicModelFilter filter, Class<Entity> clazz, Class<RO> toClazz) {
        return new ReadCRUDRequestBuilder<>(
                (context) -> {
                    PagedResult<Entity> result = crudReadHandler.indexInternal(filter, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies(), false);
                    List<RO> mappedResults = crudHelper.fillMany(result.getResults(), toClazz);
                    return PagedResult.Companion.from(result, mappedResults, result.getStart(), result.getLimit(), result.getTotal(), result.getHasMore());
                }, (context) -> crudReadHandler.indexInternal(filter, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies(), true).getTotal());
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> UpdateCRUDRequestBuilder<CRUDPreDeleteHook<ID, Entity>, CRUDOnDeleteHook<ID, Entity>, CRUDPostDeleteHook<ID, Entity>, Void> delete(ID id,
                                                                                                                                                                                                           Class<Entity> clazz) {
        return new UpdateCRUDRequestBuilder<>((context) -> {
            crudDeleteHandler.deleteInternal(id, clazz, context.getHooksDTO(), context.getApplyPolicies());
            return null;
        });
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> UpdateCRUDRequestBuilder<CRUDPreCreateFromHook<ID, Entity>, CRUDOnCreateFromHook<ID, Entity>, CRUDPostCreateFromHook<ID, Entity>, Entity> createFrom(
            Object object, Class<Entity> clazz) {
        return new UpdateCRUDRequestBuilder<>((context) -> crudCreateHandler.createFromInternal(object, clazz, context.getHooksDTO()));
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, RO> UpdateCRUDRequestBuilder<CRUDPreCreateFromHook<ID, Entity>, CRUDOnCreateFromHook<ID, Entity>, CRUDPostCreateFromHook<ID, Entity>, RO> createFrom(
            Object object, Class<Entity> clazz, Class<RO> toClazz) {
        return new UpdateCRUDRequestBuilder<>((context) -> {
            Entity result = crudCreateHandler.createFromInternal(object, clazz, context.getHooksDTO());
            return crudHelper.fill(result, toClazz);
        });
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> UpdateCRUDRequestBuilder<CRUDPreCreateHook<ID, Entity>, CRUDOnCreateHook<ID, Entity>, CRUDPostCreateHook<ID, Entity>, Entity> create(
            Entity entity) {
        return new UpdateCRUDRequestBuilder<>((context) -> crudCreateHandler.createInternal(entity, context.getHooksDTO(), context.getApplyPolicies()));
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, RO> UpdateCRUDRequestBuilder<CRUDPreCreateHook<ID, Entity>, CRUDOnCreateHook<ID, Entity>, CRUDPostCreateHook<ID, Entity>, RO> create(Entity entity,
                                                                                                                                                                                                             Class<RO> toClazz) {
        return new UpdateCRUDRequestBuilder<>((context) -> {
            Entity result = crudCreateHandler.createInternal(entity, context.getHooksDTO(), context.getApplyPolicies());
            return crudHelper.fill(result, toClazz);
        });
    }


    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> UpdateCRUDRequestBuilder<CRUDPreUpdateFromHook<ID, Entity>, CRUDOnUpdateFromHook<ID, Entity>, CRUDPostUpdateFromHook<ID, Entity>, Entity> updateFrom(
            ID id, Object object, Class<Entity> clazz) {
        return new UpdateCRUDRequestBuilder<>((context) -> crudUpdateHandler.updateFromInternal(id, object, clazz, context.getHooksDTO(), context.getApplyPolicies()));
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, RO> UpdateCRUDRequestBuilder<CRUDPreUpdateFromHook<ID, Entity>, CRUDOnUpdateFromHook<ID, Entity>, CRUDPostUpdateFromHook<ID, Entity>, RO> updateFrom(
            ID id, Object object, Class<Entity> clazz, Class<RO> toClazz) {
        return new UpdateCRUDRequestBuilder<>((context) -> {
            Entity result = crudUpdateHandler.updateFromInternal(id, object, clazz, context.getHooksDTO(), context.getApplyPolicies());
            return crudHelper.fill(result, toClazz);
        });
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> UpdateCRUDRequestBuilder<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>, Entity> update(
            Entity entity) {
        return new UpdateCRUDRequestBuilder<>((context) -> crudUpdateHandler.updateInternal(entity, context.getHooksDTO(), context.getApplyPolicies()));
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, RO> UpdateCRUDRequestBuilder<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>, RO> update(Entity entity,
                                                                                                                                                                                                             Class<RO> toClazz) {
        return new UpdateCRUDRequestBuilder<>((context) -> {
            Entity result = crudUpdateHandler.updateInternal(entity, context.getHooksDTO(), context.getApplyPolicies());
            return crudHelper.fill(result, toClazz);
        });
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> MassUpdateCRUDRequestBuilder<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>, List<Entity>> update(
            List<Entity> entities) {
        return new MassUpdateCRUDRequestBuilder<>((context) -> crudUpdateHandler.updateMany(entities, context.getHooksDTO(), context.getPersistCopy(), context.getApplyPolicies()));
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, RO> MassUpdateCRUDRequestBuilder<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>, List<RO>> update(
            List<Entity> entities, Class<RO> toClazz) {
        return new MassUpdateCRUDRequestBuilder<>((context) -> {
            List<Entity> result = crudUpdateHandler.updateMany(entities, context.getHooksDTO(), context.getPersistCopy(), context.getApplyPolicies());
            return crudHelper.fillMany(result, toClazz);
        });
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> MassUpdateCRUDRequestBuilder<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>, List<Entity>> updateByFilter(
            DynamicModelFilter filter, Class<Entity> entityClazz) {
        return new MassUpdateCRUDRequestBuilder<>((context) -> crudUpdateHandler.updateByFilter(filter, entityClazz, context.getHooksDTO(), context.getPersistCopy(), context.getApplyPolicies()));
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, RO> MassUpdateCRUDRequestBuilder<CRUDPreUpdateHook<ID, Entity>, CRUDOnUpdateHook<ID, Entity>, CRUDPostUpdateHook<ID, Entity>, List<RO>> updateByFilter(
            DynamicModelFilter filter, Class<Entity> entityClazz, Class<RO> toClazz) {
        return new MassUpdateCRUDRequestBuilder<>((context) -> {
            List<Entity> result = crudUpdateHandler.updateByFilter(filter, entityClazz, context.getHooksDTO(), context.getPersistCopy(), context.getApplyPolicies());
            return crudHelper.fillMany(result, toClazz);
        });
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> ReadCRUDRequestBuilder<CRUDPreShowByHook<ID, Entity>, CRUDOnShowByHook<ID, Entity>, CRUDPostShowByHook<ID, Entity>, Entity> showBy(
            DynamicModelFilter filter, Class<Entity> clazz) {
        return new ReadCRUDRequestBuilder<>(
                (context) -> crudReadHandler.showByInternal(filter, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies()),
                (context) -> crudReadHandler.showByInternal(filter, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies()) != null ? 1L : 0L
        );
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, RO> ReadCRUDRequestBuilder<CRUDPreShowByHook<ID, Entity>, CRUDOnShowByHook<ID, Entity>, CRUDPostShowByHook<ID, Entity>, RO> showBy(
            DynamicModelFilter filter, Class<Entity> clazz, Class<RO> toClazz) {
        return new ReadCRUDRequestBuilder<>(
                (context) -> {
                    Entity result = crudReadHandler.showByInternal(filter, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies());
                    if (result == null) {
                        return null;
                    }

                    return crudHelper.fill(result, toClazz);
                }, (context) -> crudReadHandler.showByInternal(filter, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies()) != null ? 1L : 0L
        );
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> ReadCRUDRequestBuilder<CRUDPreShowHook<ID, Entity>, CRUDOnShowHook<ID, Entity>, CRUDPostShowHook<ID, Entity>, Entity> show(ID id,
                                                                                                                                                                                                   Class<Entity> clazz) {
        return new ReadCRUDRequestBuilder<>(
                (context) -> crudReadHandler.showInternal(id, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies()),
                (context) -> crudReadHandler.showInternal(id, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies()) != null ? 1L : 0L
        );
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, RO> ReadCRUDRequestBuilder<CRUDPreShowHook<ID, Entity>, CRUDOnShowHook<ID, Entity>, CRUDPostShowHook<ID, Entity>, RO> show(ID id,
                                                                                                                                                                                                   Class<Entity> clazz, Class<RO> toClazz) {
        return new ReadCRUDRequestBuilder<>((context) -> {
            Entity result = crudReadHandler.showInternal(id, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies());
            if (result == null) {
                return null;
            }

            return crudHelper.fill(result, toClazz);
        }, (context) -> crudReadHandler.showInternal(id, clazz, context.getHooksDTO(), context.getFromCache(), context.getPersistCopy(), context.getApplyPolicies()) != null ? 1L : 0L
        );
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void validateFilter(DynamicModelFilter filter, Class<Entity> clazz) {
        crudHelper.validateAndFillFilterFieldMetadata(filter.getFilterFields(), clazz);
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> boolean filterMatches(DynamicModelFilter filter, Entity entity) {
        Objects.requireNonNull(entity, "'entity' cannot be null");
        validateFilter(filter, entity.getClass());
        return DynamicModelFilterUtils.filtersMatch(filter, entity);
    }
}
