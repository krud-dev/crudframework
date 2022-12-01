package dev.krud.crudframework.crud.hooks.update.from;

import dev.krud.crudframework.crud.hooks.base.CRUDHook;
import dev.krud.crudframework.model.BaseCrudEntity;

import java.io.Serializable;

@FunctionalInterface
public interface CRUDPreUpdateFromHook<ID extends Serializable, Entity extends BaseCrudEntity<ID>> extends CRUDHook {

	void run(ID id, Object object);
}
