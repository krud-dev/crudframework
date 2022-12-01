package dev.krud.crudframework.crud.model;

import dev.krud.crudframework.crud.hooks.base.CRUDHook;
import dev.krud.crudframework.model.BaseCrudEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract CRUD request builder
 *
 * @param <PreHook> The {@link CRUDHook} that is run pre CRUD action
 * @param <OnHook> The {@link CRUDHook} that is run on(inside a transaction) CRUD action
 * @param <PostHook> The {@link CRUDHook} that is run post CRUD action
 * @param <EntityType> the {@link BaseCrudEntity} return type
 */
public abstract class CRUDRequestBuilder<PreHook, OnHook, PostHook, EntityType> {

	protected List<PreHook> preHooks = new ArrayList<>();

	protected List<OnHook> onHooks = new ArrayList<>();

	protected List<PostHook> postHooks = new ArrayList<>();

	protected boolean applyPolicies = false;

	/**
	 * Runs the CRUD action
	 *
	 * @return the entity type
	 */
	public abstract EntityType execute();

	/**
	 * Adds a preHook to the request
	 *
	 * @param preHook the pre hook
	 */
	public final CRUDRequestBuilder<PreHook, OnHook, PostHook, EntityType> withPreHook(PreHook preHook) {
		preHooks.add(preHook);
		return this;
	}

	/**
	 * Adds an onHook to the request
	 *
	 * @param onHook the on hook
	 */
	public final CRUDRequestBuilder<PreHook, OnHook, PostHook, EntityType> withOnHook(OnHook onHook) {
		onHooks.add(onHook);
		return this;
	}

	public CRUDRequestBuilder<PreHook, OnHook, PostHook, EntityType> enforceAccess(BaseCrudEntity requester) {
        // todo:
		return this;
	}

	public CRUDRequestBuilder<PreHook, OnHook, PostHook, EntityType> enforceAccess(Class<?> requesterType, Long requesterId) {
        // todo:
		return this;
	}

	public CRUDRequestBuilder<PreHook, OnHook, PostHook, EntityType> applyPolicies() {
		applyPolicies = true;
		return this;
	}

	/**
	 * Adds a postHook to the request
	 *
	 * @param postHook the post hook
	 */
	public final CRUDRequestBuilder<PreHook, OnHook, PostHook, EntityType> withPostHook(PostHook postHook) {
		postHooks.add(postHook);
		return this;
	}

}
