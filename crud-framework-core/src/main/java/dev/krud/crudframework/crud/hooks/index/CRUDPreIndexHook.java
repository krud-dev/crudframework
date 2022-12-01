package dev.krud.crudframework.crud.hooks.index;

import dev.krud.crudframework.crud.hooks.base.CRUDHook;
import dev.krud.crudframework.model.BaseCrudEntity;
import dev.krud.crudframework.modelfilter.DynamicModelFilter;

import java.io.Serializable;

@FunctionalInterface
public interface CRUDPreIndexHook<ID extends Serializable, Entity extends BaseCrudEntity<ID>> extends CRUDHook {

	void run(DynamicModelFilter filter);
}
