package dev.krud.crudframework.crud.hooks.base;

import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;
import dev.krud.crudframework.ro.PagingDTO;

import java.io.Serializable;

public interface FilterPagingDTOHook<ID extends Serializable, Entity extends BaseCrudEntity<ID>> extends CRUDHook {

	void run(DynamicModelFilter filter, PagingDTO<Entity> result);
}
