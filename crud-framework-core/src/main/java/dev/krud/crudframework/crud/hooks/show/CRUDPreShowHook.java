package dev.krud.crudframework.crud.hooks.show;

import dev.krud.crudframework.crud.hooks.base.IDHook;
import dev.krud.crudframework.model.BaseCrudEntity;

import java.io.Serializable;

@FunctionalInterface
public interface CRUDPreShowHook<ID extends Serializable, Entity extends BaseCrudEntity<ID>> extends IDHook<ID> {

}
