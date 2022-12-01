package dev.krud.crudframework.mongo.annotation

import org.springframework.context.annotation.Import
import dev.krud.crudframework.crud.annotation.EnableCrudFramework
import dev.krud.crudframework.mongo.config.CrudMongoConnectorConfiguration

@Target(AnnotationTarget.CLASS)
@Import(CrudMongoConnectorConfiguration::class)
@EnableCrudFramework
annotation class EnableMongoCrud