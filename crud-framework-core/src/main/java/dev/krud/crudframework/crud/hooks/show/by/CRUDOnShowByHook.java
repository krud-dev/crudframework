package dev.krud.crudframework.crud.hooks.show.by;

import dev.krud.crudframework.crud.hooks.base.EntityHook;
import dev.krud.crudframework.model.BaseCrudEntity;

import java.io.Serializable;

@FunctionalInterface
public interface CRUDOnShowByHook<ID extends Serializable, Entity extends BaseCrudEntity<ID>> extends EntityHook<ID, Entity> {

}
