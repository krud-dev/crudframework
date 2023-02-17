package dev.krud.crudframework.mongo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import dev.krud.crudframework.modelfilter.FilterField;
import dev.krud.crudframework.modelfilter.OrderDTO;
import dev.krud.crudframework.modelfilter.enums.FilterFieldOperation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class AbstractMongoBaseDao {

	@Autowired
	protected MongoTemplate mongoTemplate;

	private static final Set<Class<?>> primitiveAndBoxedTypes = new HashSet<>();

	static {
		primitiveAndBoxedTypes.add(boolean.class);
		primitiveAndBoxedTypes.add(byte.class);
		primitiveAndBoxedTypes.add(short.class);
		primitiveAndBoxedTypes.add(char.class);
		primitiveAndBoxedTypes.add(int.class);
		primitiveAndBoxedTypes.add(long.class);
		primitiveAndBoxedTypes.add(float.class);
		primitiveAndBoxedTypes.add(double.class);
		primitiveAndBoxedTypes.add(Boolean.class);
		primitiveAndBoxedTypes.add(Character.class);
		primitiveAndBoxedTypes.add(Byte.class);
		primitiveAndBoxedTypes.add(Short.class);
		primitiveAndBoxedTypes.add(String.class);
		primitiveAndBoxedTypes.add(Integer.class);
		primitiveAndBoxedTypes.add(Long.class);
		primitiveAndBoxedTypes.add(Float.class);
		primitiveAndBoxedTypes.add(Double.class);
	}

	/**
	 * Return entity by given class and UUID.
	 *
	 * @param clazz required class
	 * @param id unique identificator
	 * @return entity with given class and UUID.
	 */
	public <T extends BaseCrudEntity> T findObject(Class<T> clazz, Serializable id) {
		Criteria criteria = Criteria.where("id").is(id);
		return mongoTemplate.findOne(new Query().addCriteria(criteria), clazz);
	}

	/**
	 * Return entity list by given class and a list of UUIDs.
	 *
	 * @param clazz required class
	 * @param ids a list of UUIDs
	 * @return a list of entities with given class and matching UUID
	 */
	public <T extends BaseCrudEntity> List<T> findObjectByIds(Class<T> clazz, Serializable ids[]) {
		Criteria criteria = Criteria.where("id").in(ids);
		return mongoTemplate.find(new Query().addCriteria(criteria), clazz);
	}

	/**
	 * Delete entity with given class and UUID from DB
	 *
	 * @param clazz required class
	 * @param id unique identificator
	 */
	public void deleteObject(Class clazz, Serializable id) {
		deleteObject(findObject(clazz, id));
	}

	/**
	 * Delete entity from DB.
	 *
	 * @param object entity for removing.
	 */
	public void deleteObject(Object object) {
		if(null != object) {
			mongoTemplate.remove(object);
		}
	}

	protected Query buildQuery(DynamicModelFilter filter) {
		Query query = new Query();

		if(filter != null) {
			if(filter.getFilterFields() != null && !filter.getFilterFields().isEmpty()) {
				Criteria criteria = new Criteria();
				for(FilterField filterField : filter.getFilterFields()) {
					buildCriterion(criteria, filterField);
				}
				query.addCriteria(criteria);
			}

		}
		return query;
	}


	private Criteria buildCriterion(Criteria criteria, FilterField filterField) {
		if(filterField.getChildren() != null || isValidSimpleFilterField(filterField)) {

			switch(filterField.getOperation()) {
				case Equal:
					criteria.and(filterField.getFieldName()).is(filterField.value1());
					break;
				case NotEqual:
					criteria.and(filterField.getFieldName()).ne(filterField.value1());
					break;
				case In:
					criteria.and(filterField.getFieldName()).in(filterField.getValues());
					break;
				case NotIn:
					criteria.and(filterField.getFieldName()).nin(filterField.getValues());
					break;
				case GreaterEqual:
					criteria.and(filterField.getFieldName()).gte(filterField.value1());
					break;
				case GreaterThan:
					criteria.and(filterField.getFieldName()).gt(filterField.value1());
					break;
				case LowerEqual:
					criteria.and(filterField.getFieldName()).lte(filterField.value1());
					break;
				case LowerThan:
					criteria.and(filterField.getFieldName()).lt(filterField.value1());
					break;
				case Between:
					criteria.and(filterField.getFieldName())
									.gte(filterField.value1())
									.and(filterField.getFieldName())
									.lte(filterField.value2());
					break;
				case Contains:
					if(filterField.value1() != null && !filterField.value1().toString().trim().isEmpty()) {
						criteria.and(filterField.getFieldName()).regex(Pattern.compile(filterField.value1().toString(), Pattern.LITERAL & Pattern.CASE_INSENSITIVE));
					}
					break;
				case IsNull:
					criteria.orOperator(
							Criteria.where(filterField.getFieldName()).exists(false),
							Criteria.where(filterField.getFieldName()).is(null)
					);
					break;
				case IsNotNull:
					criteria.and(filterField.getFieldName()).ne(null);
					break;
				case And:
					if(filterField.getChildren() != null && !filterField.getChildren().isEmpty()) {
						List<Criteria> criterias = new ArrayList<>();
						for(FilterField child : filterField.getChildren()) {
							criterias.add(buildCriterion(criteria, child));
						}

						criteria.andOperator(criterias.toArray(new Criteria[]{}));
					}
					break;
				case Or:
					if(filterField.getChildren() != null && !filterField.getChildren().isEmpty()) {
						List<Criteria> criterias = new ArrayList<>();
						for(FilterField child : filterField.getChildren()) {
							criterias.add(buildCriterion(criteria, child));
						}

						criteria.orOperator(criterias.toArray(new Criteria[]{}));
					}
					break;
				case Not:
					if(filterField.getChildren() != null && !filterField.getChildren().isEmpty()) {
						FilterField child = filterField.getChildren().get(0);
						criteria.not().andOperator(buildCriterion(criteria, child));
					}
					break;
				case Noop:
					criteria.and("id").in();
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported filter operation: " + filterField.getOperation());
			}
		}

		return criteria;
	}

	private boolean isValidSimpleFilterField(FilterField filterField) {
		return filterField.getFieldName() != null && !filterField.getFieldName().trim().isEmpty() &&
				filterField.getOperation() != null && areValuesValid(filterField);
	}

	private boolean areValuesValid(FilterField filterField) {
		List<FilterFieldOperation> simpleOperationsWithoutValue = Arrays.asList(FilterFieldOperation.IsNotNull, FilterFieldOperation.IsNull);
		if(simpleOperationsWithoutValue.contains(filterField.getOperation())) {
			return true;
		} else {
			return filterField.getValues() != null && filterField.getValues().length > 0;
		}
	}

	protected void setOrder(Query query, Set<OrderDTO> orders) {
		List<Sort.Order> sortOrders = new ArrayList<>();
		for (OrderDTO order : orders) {
			if(order.getBy() != null && !order.getBy().trim().isEmpty()) {
				sortOrders.add(new Sort.Order(order.getDescending() ? Sort.Direction.DESC : Sort.Direction.ASC, order.getBy()));
			}
		}
		if(!sortOrders.isEmpty()) {
			query.with(Sort.by(sortOrders));
		} else {
			query.with(Sort.by(
					Sort.Direction.DESC,
					"id"
			));
		}
	}

	protected void setBoundaries(Query query, Integer start, Integer limit) {
		if(start != null) {
			query.skip(start);
		}

		if(limit != null) {
			query.limit(limit);
		}
	}

	protected String replaceSpecialCharacters(String string) {
		return string.replace("_", "\\_").replace("%", "\\%");
	}
}
