package dev.krud.crudframework.jpa.lazyinitializer.annotation

import dev.krud.crudframework.crud.annotation.WithHooks
import dev.krud.crudframework.jpa.lazyinitializer.LazyInitializerPersistentHooks

@WithHooks(hooks = [LazyInitializerPersistentHooks::class])
annotation class DynamicLazyInitialization