package dev.krud.crudframework.web.rest

import dev.krud.crudframework.model.BaseCrudEntity
import java.io.Serializable
import kotlin.reflect.KClass

data class CrudControllerDefinition(
    val annotation: CrudController,
    val clazz: KClass<BaseCrudEntity<out Serializable>>
)