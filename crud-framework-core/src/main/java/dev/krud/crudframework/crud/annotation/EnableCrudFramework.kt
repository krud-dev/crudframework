package dev.krud.crudframework.crud.annotation

import dev.krud.crudframework.crud.configuration.CrudCacheConfiguration
import dev.krud.crudframework.crud.configuration.CrudFrameworkConfiguration
import org.springframework.context.annotation.Import

/**
 * Used to enable the framework
 * Enables the component map and post startup features
 * Additionally triggers [CrudFrameworkConfiguration]
 */
@Target(AnnotationTarget.CLASS)
@Import(CrudFrameworkConfiguration::class, CrudCacheConfiguration::class)
annotation class EnableCrudFramework