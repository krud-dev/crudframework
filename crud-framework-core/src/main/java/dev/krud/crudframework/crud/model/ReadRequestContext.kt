package dev.krud.crudframework.crud.model

import dev.krud.crudframework.crud.hooks.HooksDTO

data class ReadRequestContext<PreHook, OnHook, PostHook, EntityType>(
    val hooksDTO: HooksDTO<PreHook, OnHook, PostHook>,
    val fromCache: Boolean,
    val persistCopy: Boolean,
    val applyPolicies: Boolean
)