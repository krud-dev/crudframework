package dev.krud.crudframework.jpa.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import dev.krud.crudframework.crud.handler.CrudDao
import dev.krud.crudframework.jpa.dao.CrudDaoImpl
import dev.krud.crudframework.jpa.lazyinitializer.LazyInitializerPersistentHooks

@Configuration
class CrudHibernate5ConnectorConfiguration {
    @Bean
    fun jpaCrudDao(): CrudDao = CrudDaoImpl()

    @Bean
    fun lazyInitializerPersistentHooks(): LazyInitializerPersistentHooks = LazyInitializerPersistentHooks()
}