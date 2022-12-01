package dev.krud.crudframework.jpa.dao;

import org.hibernate.Criteria;
import dev.krud.crudframework.crud.handler.CrudDao;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;

import java.io.Serializable;
import java.util.List;

public class CrudDaoImpl extends AbstractBaseDao implements CrudDao {

	@Override
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, E extends DynamicModelFilter> List<Entity> index(E filter, Class<Entity> clazz) {
		Criteria criteria = buildCriteria(filter, clazz);
		setOrder(criteria, filter);
		setBoundaries(criteria, filter.getStart(), filter.getLimit());
		return criteria.list();
	}

	@Override
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>, E extends DynamicModelFilter> long indexCount(E filter, Class<Entity> clazz) {
		Criteria criteria = buildCriteria(filter, clazz);
		return getCriteriaCount(criteria);
	}

	@Override
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> void hardDeleteById(ID id, Class<Entity> clazz) {
		deleteObject(clazz, id);
	}

	@Override
	public <ID extends Serializable, Entity extends BaseCrudEntity<ID>> Entity saveOrUpdate(Entity entity) {
		getCurrentSession().saveOrUpdate(entity);
		return entity;
	}
}
