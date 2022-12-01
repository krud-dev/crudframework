package dev.krud.crudframework.crud.hooks.interfaces;

import dev.krud.crudframework.crud.annotation.WithHooks;
import dev.krud.crudframework.crud.model.ReadCRUDRequestBuilder;
import org.jetbrains.annotations.Nullable;
import dev.krud.crudframework.model.BaseCrudEntity;

import java.io.Serializable;

/**
 * This class contains all hook callbacks for the Show operation. It can be implemented
 * as many times as needed per entity, or even for abstract entities for use with {@link WithHooks}. Implementations of this interface should be declared as Spring beans.
 * @param <Entity> the entity to listen to
 * @param <ID> the ID type of the entity
 */
public interface ShowHooks<ID extends Serializable, Entity extends BaseCrudEntity<ID>> extends CRUDHooks<ID, Entity> {

	/**
	 * Called prior to a Show operation.
	 * The validity of the ID at this point is still unknown.
	 * @param id represents the ID of the entity to be shown
	 */
	default void preShow(ID id) {
	}

	/**
	 * Called during a Show operation.
	 * If {@link ReadCRUDRequestBuilder#fromCache} is used, this method will be skipped. It will be called after data access checks and run inside of a read-only transaction.
	 * @param entity represents the entity that was found, or null if not found
	 */
	default void onShow(@Nullable Entity entity) {
	}

	/**
	 * Called after a Show operation.
	 * @param entity represents the entity that was found, or null if not found
	 */
	default void postShow(@Nullable Entity entity) {
	}
}
