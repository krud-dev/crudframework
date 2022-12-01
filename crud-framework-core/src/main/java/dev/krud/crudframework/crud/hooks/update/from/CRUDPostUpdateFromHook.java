package dev.krud.crudframework.crud.hooks.update.from;

import dev.krud.crudframework.crud.hooks.base.EntityHook;
import dev.krud.crudframework.model.BaseCrudEntity;

import java.io.Serializable;

@FunctionalInterface
public interface CRUDPostUpdateFromHook<ID extends Serializable, Entity extends BaseCrudEntity<ID>> extends EntityHook<ID, Entity> {

}
