package dev.krud.crudframework.crud.hooks.update;

import dev.krud.crudframework.crud.hooks.base.EntityHook;
import dev.krud.crudframework.model.BaseCrudEntity;

import java.io.Serializable;

@FunctionalInterface
public interface CRUDPostUpdateHook<ID extends Serializable, Entity extends BaseCrudEntity<ID>> extends EntityHook<ID, Entity> {

}
