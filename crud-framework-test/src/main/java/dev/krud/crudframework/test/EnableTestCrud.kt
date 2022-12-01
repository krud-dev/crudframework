package dev.krud.crudframework.test

import org.springframework.context.annotation.Import
import dev.krud.crudframework.crud.annotation.EnableCrudFramework

/**
 * Enable the Crud Framework with the `TestCrudDaoImpl` dao
 */
@EnableCrudFramework
@Import(TestCrudConfiguration::class)
annotation class EnableTestCrud()
