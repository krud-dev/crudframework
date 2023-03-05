package dev.krud.crudframework.mongo.config

import dev.krud.crudframework.crud.handler.CrudDao
import dev.krud.crudframework.mongo.dao.MongoCrudDaoImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CrudMongoConnectorConfiguration {
    @Bean
    fun mongoCrudDao(): CrudDao = MongoCrudDaoImpl()
}