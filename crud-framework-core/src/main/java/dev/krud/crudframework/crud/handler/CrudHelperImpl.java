package dev.krud.crudframework.crud.handler;

import dev.krud.crudframework.FieldUtils;
import dev.krud.crudframework.crud.cache.CacheManagerAdapter;
import dev.krud.crudframework.crud.cache.CacheUtils;
import dev.krud.crudframework.crud.cache.CrudCache;
import dev.krud.crudframework.crud.cache.CrudCacheOptions;
import dev.krud.crudframework.crud.exception.CrudException;
import dev.krud.crudframework.crud.exception.CrudInvalidStateException;
import dev.krud.crudframework.crud.exception.CrudTransformationException;
import dev.krud.crudframework.crud.hooks.interfaces.CRUDHooks;
import dev.krud.crudframework.crud.model.EntityCacheMetadata;
import dev.krud.crudframework.crud.model.EntityMetadataDTO;
import dev.krud.crudframework.exception.WrapException;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import dev.krud.crudframework.modelfilter.FilterField;
import dev.krud.crudframework.modelfilter.FilterFields;
import dev.krud.crudframework.modelfilter.enums.FilterFieldDataType;
import dev.krud.crudframework.modelfilter.enums.FilterFieldOperation;
import dev.krud.crudframework.util.ReflectionUtils;
import dev.krud.shapeshift.ShapeShift;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CrudHelperImpl implements CrudHelper, InitializingBean {

    /**
     * Dependencies
     */
    private final List<CrudDao> crudDaos;
    private final ApplicationContext applicationContext;

    private final CacheManagerAdapter cacheManagerAdapter;

    private final ShapeShift shapeShift;

    /**
     * Others
     */

    private final Map<Class<? extends BaseCrudEntity<?>>, CrudDao> crudDaoMap = new ConcurrentHashMap<>();

    private final Map<Class<? extends BaseCrudEntity<?>>, EntityMetadataDTO> entityMetadataDTOs = new ConcurrentHashMap<>();

    private final Map<String, CrudCache> cacheMap = new HashMap<>();

    private CrudCache pagingCache;

    public CrudHelperImpl(@Autowired(required = false) List<CrudDao> crudDaos, ApplicationContext applicationContext, CacheManagerAdapter cacheManagerAdapter, ShapeShift shapeShift) {
        this.crudDaos = crudDaos;
        this.applicationContext = applicationContext;
        this.cacheManagerAdapter = cacheManagerAdapter;
        this.shapeShift = shapeShift;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pagingCache = cacheManagerAdapter.createCache("pagingCache",
                new CrudCacheOptions(
                        60L,
                        60L,
                        10000L
                ));
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, HooksType extends CRUDHooks> List<HooksType> getHooks(Class<HooksType> crudHooksClazz, Class<Entity> entityClazz) {
        EntityMetadataDTO metadataDTO = getEntityMetadata(entityClazz);
        Set<HooksType> matchingAnnotationHooks = (Set<HooksType>) metadataDTO.getHooksFromAnnotations()
                .stream()
                .filter(hook -> crudHooksClazz.isAssignableFrom(hook.getClass()))
                .collect(Collectors.toSet());
        List<HooksType> hooks = applicationContext.getBeansOfType(crudHooksClazz).values()
                .stream()
                .filter(c -> c.getType() == entityClazz)
                .collect(Collectors.toList());
        hooks.addAll(matchingAnnotationHooks);
        return hooks;
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> boolean isEntityDeleted(Entity entity) {
        if (entity == null) {
            return true;
        }

        Class<Entity> clazz = (Class<Entity>) entity.getClass();

        EntityMetadataDTO metadataDTO = getEntityMetadata(clazz);

        if (metadataDTO.getDeleteableType() == EntityMetadataDTO.DeleteableType.Hard) {
            return false;
        }

        if (metadataDTO.getDeleteField() == null) {
            return false;
        }

        ReflectionUtils.makeAccessible(metadataDTO.getDeleteField());
        return (boolean) ReflectionUtils.getField(metadataDTO.getDeleteField(), entity);
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void decorateFilter(DynamicModelFilter filter, Class<Entity> entityClazz) {
        EntityMetadataDTO metadataDTO = getEntityMetadata(entityClazz);
        if (metadataDTO.getDeleteableType() == EntityMetadataDTO.DeleteableType.Soft) {
            Field deleteField = metadataDTO.getDeleteField();
            if (deleteField != null) {
                filter.add(FilterFields.eq(deleteField.getName(), FilterFieldDataType.Boolean, false));
            }
        }

        validateAndFillFilterFieldMetadata(filter.getFilterFields(), entityClazz);
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void validateAndFillFilterFieldMetadata(List<FilterField> filterFields, Class<Entity> entityClazz) {
        EntityMetadataDTO metadataDTO = getEntityMetadata(entityClazz);
        for (FilterField filterField : filterFields) {
            if (filterField.validated()) {
                continue;
            }
            if (filterField.getOperation() == null) {
                throw new IllegalStateException("A FilterField must have an operation");
            }

            boolean isJunction = filterField.getOperation() == FilterFieldOperation.And || filterField.getOperation() == FilterFieldOperation.Or || filterField.getOperation() == FilterFieldOperation.Not;
            if (isJunction) {
                if (filterField.getChildren() != null && !filterField.getChildren().isEmpty()) {
                    validateAndFillFilterFieldMetadata(filterField.getChildren(), entityClazz);
                }
            } else {
                if (filterField.getFieldName() != null) {
                    String fieldName = filterField.getFieldName();
                    if (fieldName.endsWith(".elements")) {
                        fieldName = fieldName.substring(0, fieldName.lastIndexOf(".elements"));
                    }
                    if (!metadataDTO.getFields().containsKey(fieldName)) {
                        throw new RuntimeException("Cannot filter field [ " + fieldName + " ] as it was not found on entity [ " + metadataDTO.getSimpleName() + " ]");
                    }

                    Field field = metadataDTO.getFields().get(fieldName).getField();
                    Class<?> fieldClazz = field.getType();

                    if (Collection.class.isAssignableFrom(field.getType())) {
                        Class<?> potentialFieldClazz = FieldUtils.getGenericClass(field, 0);

                        if (potentialFieldClazz != null) {
                            fieldClazz = potentialFieldClazz;
                        }
                    }

                    FilterFieldDataType fieldDataType = getDataTypeFromClass(fieldClazz);
                    filterField.setDataType(fieldDataType);
                    if (fieldDataType == FilterFieldDataType.Enum) {
                        filterField.setEnumType(fieldClazz.getName());
                    }
                }
            }
            filterField.validate();
        }
    }

    private FilterFieldDataType getDataTypeFromClass(Class clazz) {
        if (String.class.equals(clazz)) {
            return FilterFieldDataType.String;
        } else if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
            return FilterFieldDataType.Integer;
        } else if (long.class.equals(clazz) || Long.class.equals(clazz)) {
            return FilterFieldDataType.Long;
        } else if (double.class.equals(clazz) || Double.class.equals(clazz)) {
            return FilterFieldDataType.Double;
        } else if (Date.class.equals(clazz)) {
            return FilterFieldDataType.Date;
        } else if (boolean.class.equals(clazz) || Boolean.class.equals(clazz)) {
            return FilterFieldDataType.Boolean;
        } else if (Enum.class.isAssignableFrom(clazz)) {
            return FilterFieldDataType.Enum;
        } else if (UUID.class.equals(clazz)) {
            return FilterFieldDataType.UUID;
        }

        return FilterFieldDataType.Object;
    }

    /* transactional */
    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> List<Entity> getEntities(DynamicModelFilter filter, Class<Entity> entityClazz, Boolean persistCopy) {
        decorateFilter(filter, entityClazz);

        if (persistCopy == null) {
            persistCopy = getEntityMetadata(entityClazz).getAlwaysPersistCopy();
        }

        List<Entity> result = getCrudDaoForEntity(entityClazz).index(filter, entityClazz);
        if (persistCopy) {
            result.forEach(BaseCrudEntity::saveOrGetCopy);
        }

        return result;
    }

    /* transactional */
    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> long getEntitiesCount(DynamicModelFilter filter, Class<Entity> entityClazz, boolean forUpdate) {
        decorateFilter(filter, entityClazz);
        return getCrudDaoForEntity(entityClazz).indexCount(filter, entityClazz);
    }

    /* transactional */
    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity getEntity(DynamicModelFilter filter, Class<Entity> entityClazz, Boolean persistCopy) {
        List<Entity> entities = getEntities(filter, entityClazz, persistCopy);
        Entity entity = null;
        if (entities.size() > 0) {
            entity = entities.get(0);
        }

        return entity;
    }

    /* transactional */
    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> long getEntityCountById(ID entityId, Class<Entity> entityClazz, boolean forUpdate) {
        FilterFieldDataType entityIdDataType = FilterFieldDataType.get(entityId.getClass());
        Objects.requireNonNull(entityIdDataType, "Could not assert entityId type");

        DynamicModelFilter filter = new DynamicModelFilter()
                .add(FilterFields.eq("id", entityIdDataType, entityId));

        return getEntitiesCount(filter, entityClazz, forUpdate);
    }


    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void checkEntityImmutability(Class<Entity> clazz) {
        EntityMetadataDTO metadataDTO = getEntityMetadata(clazz);
        if (metadataDTO.getImmutable()) {
            throw new CrudInvalidStateException("Entity of type [ " + clazz.getSimpleName() + " ] is immutable");
        }
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void checkEntityDeletability(Class<Entity> clazz) {
        EntityMetadataDTO metadataDTO = getEntityMetadata(clazz);
        if (metadataDTO.getDeleteableType() == EntityMetadataDTO.DeleteableType.None) {
            throw new CrudInvalidStateException("Entity of type [ " + clazz.getSimpleName() + " ] can not be deleted");
        }

        if (metadataDTO.getDeleteableType() == EntityMetadataDTO.DeleteableType.Soft) {
            if (metadataDTO.getDeleteField() == null) {
                throw new CrudInvalidStateException("Entity of type [ " + clazz.getSimpleName() + " ] is set for soft delete but is missing @DeleteColumn");
            }

            if (!ClassUtils.isAssignable(boolean.class, metadataDTO.getDeleteField().getType())) {
                throw new CrudInvalidStateException("Entity of type [ " + clazz.getSimpleName() + " ] has an invalid @DeleteColumn - column must be of type boolean");
            }
        }
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> EntityMetadataDTO getEntityMetadata(Class<Entity> entityClazz) {
        return entityMetadataDTOs.computeIfAbsent(entityClazz, x -> {
            EntityMetadataDTO metadataDTO = new EntityMetadataDTO(entityClazz);
            for (Class<CRUDHooks<?, ?>> hookType : metadataDTO.getHookTypesFromAnnotations()) {
                try {
                    CRUDHooks<ID, Entity> hooks = (CRUDHooks<ID, Entity>) applicationContext.getBean(hookType);
                    metadataDTO.getHooksFromAnnotations().add(hooks);
                } catch (BeansException e) {
                    throw new CrudInvalidStateException("Could not get bean for persistent hooks class of type [ " + hookType.getCanonicalName() + " ]. Error: " + e.getMessage());
                }
            }
            return metadataDTO;
        });
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void evictEntityFromCache(Entity entity) {
        Objects.requireNonNull(entity, "entity cannot be null");

        CrudCache cache = getEntityCache(entity.getClass());

        if (cache == null) {
            return;
        }

        CacheUtils.removeFromCacheIfKeyContains(cache, entity.getCacheKey());
    }

    @Override
    @WrapException(CrudException.class)
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> CrudCache getEntityCache(Class<Entity> clazz) {
        if (cacheMap.containsKey(clazz.getName())) {
            return cacheMap.get(clazz.getName());
        }

        EntityMetadataDTO dto = getEntityMetadata(clazz);
        EntityCacheMetadata cacheMetadata = dto.getCacheMetadata();
        if (cacheMetadata == null) {
            cacheMap.put(clazz.getName(), null);
            return null;
        }

        CrudCache cache = cacheManagerAdapter.getCache(cacheMetadata.getName());
        if (cache == null) {
            if (cacheMetadata.getCreateIfMissing()) {
                cache = cacheManagerAdapter.createCache(cacheMetadata.getName(), cacheMetadata.getOptions());
            } else {
                throw new CrudException("Cache for entity [ " + clazz.getSimpleName() + " ] with name [ " + dto.getCacheMetadata().getName() + " ] not found");
            }

        }
        cacheMap.put(clazz.getName(), cache);

        return cache;
    }

    @Override
    @WrapException(CrudTransformationException.class)
    public <From, To> To fill(From fromObject, Class<To> toClazz) {
        Objects.requireNonNull(fromObject, "fromObject cannot be null");
        Objects.requireNonNull(toClazz, "toClazz cannot be null");

        To toObject = shapeShift.map(fromObject, toClazz);
        return toObject;
    }

    @Override
    @WrapException(CrudTransformationException.class)
    public <From, To> void fill(From fromObject, To toObject) {
        Objects.requireNonNull(fromObject, "fromObject cannot be null");
        Objects.requireNonNull(toObject, "toObject cannot be null");

        shapeShift.map(fromObject, toObject);
    }

    @Override
    @WrapException(CrudTransformationException.class)
    public <From, To> List<To> fillMany(List<From> fromObjects, Class<To> toClazz) {
        return shapeShift.mapCollection(fromObjects, toClazz);
    }

    @Override
    public <Entity> void setTotalToPagingCache(Class<Entity> entityClazz, DynamicModelFilter filter, long total) {
        String cacheKey = entityClazz.getName() + "_" + filter.getFilterFields().hashCode();
        pagingCache.put(cacheKey, total);
    }

    @Override
    public <Entity> Long getTotalFromPagingCache(Class<Entity> entityClazz, DynamicModelFilter filter) {
        String cacheKey = entityClazz.getName() + "_" + filter.getFilterFields().hashCode();
        return (Long) pagingCache.get(cacheKey);
    }

    @Override
    public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> CrudDao getCrudDaoForEntity(Class<Entity> entityClazz) {
        return crudDaoMap.computeIfAbsent(entityClazz, x -> {
            Class entityDaoClazz = getEntityMetadata(entityClazz).getDaoClazz();
            for (CrudDao dao : crudDaos) {
                if (getTrueProxyClass(dao).equals(entityDaoClazz)) {
                    return dao;
                }
            }
            return null;
        });
    }

    private <T> Class<T> getTrueProxyClass(T proxy) {
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            try {
                return (Class<T>) ((Advised) proxy).getTargetSource().getTarget().getClass();
            } catch (Exception e) {
                return null;
            }
        } else {
            return (Class<T>) ClassUtils.getUserClass(proxy.getClass());
        }
    }
}
