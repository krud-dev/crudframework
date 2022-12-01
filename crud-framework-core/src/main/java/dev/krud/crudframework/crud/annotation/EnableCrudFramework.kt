package dev.krud.crudframework.crud.annotation

import org.springframework.context.annotation.Import
import dev.krud.crudframework.crud.configuration.CrudCacheConfiguration
import dev.krud.crudframework.crud.configuration.CrudFrameworkConfiguration
import dev.krud.crudframework.util.startup.annotation.EnablePostStartup

/**
 * Used to enable the framework
 * Enables the component map and post startup features
 * Additionally triggers [CrudFrameworkConfiguration]
 */
@Target(AnnotationTarget.CLASS)
@Import(CrudFrameworkConfiguration::class, CrudCacheConfiguration::class)
@EnablePostStartup
annotation class EnableCrudFramework