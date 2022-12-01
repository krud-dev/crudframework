package dev.krud.crudframework.mongo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import dev.krud.crudframework.crud.handler.CrudDao
import dev.krud.crudframework.mongo.dao.MongoCrudDaoImpl

@Configuration
class CrudMongoConnectorConfiguration {
    @Bean
    fun mongoCrudDao(): CrudDao = MongoCrudDaoImpl()
}