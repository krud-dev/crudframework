package dev.krud.crudframework.crud.hooks.create.from;

import dev.krud.crudframework.crud.hooks.base.ObjectEntityHook;
import dev.krud.crudframework.model.BaseCrudEntity;

import java.io.Serializable;

@FunctionalInterface
public interface CRUDOnCreateFromHook<ID extends Serializable, Entity extends BaseCrudEntity<ID>> extends ObjectEntityHook<ID, Entity> {

}
