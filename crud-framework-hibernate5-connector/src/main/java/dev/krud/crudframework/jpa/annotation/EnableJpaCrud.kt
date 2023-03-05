package dev.krud.crudframework.jpa.annotation

import dev.krud.crudframework.crud.annotation.EnableCrudFramework
import dev.krud.crudframework.jpa.config.CrudHibernate5ConnectorConfiguration
import org.springframework.context.annotation.Import

@EnableCrudFramework
@Import(CrudHibernate5ConnectorConfiguration::class)
annotation class EnableJpaCrud