package dev.krud.crudframework.test

import dev.krud.crudframework.crud.annotation.EnableCrudFramework
import org.springframework.context.annotation.Import

/**
 * Enable the Crud Framework with the `TestCrudDaoImpl` dao
 */
@EnableCrudFramework
@Import(TestCrudConfiguration::class)
annotation class EnableTestCrud()