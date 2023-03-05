package dev.krud.crudframework.mongo.annotation

import dev.krud.crudframework.crud.annotation.EnableCrudFramework
import dev.krud.crudframework.mongo.config.CrudMongoConnectorConfiguration
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Import(CrudMongoConnectorConfiguration::class)
@EnableCrudFramework
annotation class EnableMongoCrud