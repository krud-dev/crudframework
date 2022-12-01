package dev.krud.crudframework.jpa.annotation

import org.springframework.context.annotation.Import
import dev.krud.crudframework.crud.annotation.EnableCrudFramework
import dev.krud.crudframework.jpa.config.CrudHibernate5ConnectorConfiguration

@EnableCrudFramework
@Import(CrudHibernate5ConnectorConfiguration::class)
annotation class EnableJpaCrud