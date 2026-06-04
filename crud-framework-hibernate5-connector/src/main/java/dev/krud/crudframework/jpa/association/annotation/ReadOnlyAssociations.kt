package dev.krud.crudframework.jpa.association.annotation

import dev.krud.crudframework.crud.annotation.WithHooks
import dev.krud.crudframework.jpa.association.ReadOnlyAssociationsPersistentHooks

@WithHooks(hooks = [ReadOnlyAssociationsPersistentHooks::class])
annotation class ReadOnlyAssociations
