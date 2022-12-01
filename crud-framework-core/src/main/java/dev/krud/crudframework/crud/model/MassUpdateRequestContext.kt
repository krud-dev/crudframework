package dev.krud.crudframework.crud.model

import dev.krud.crudframework.crud.hooks.HooksDTO

data class MassUpdateRequestContext<PreHook, OnHook, PostHook, EntityType>(
    val hooksDTO: HooksDTO<PreHook, OnHook, PostHook>,
    val persistCopy: Boolean,
    val applyPolicies: Boolean
)