package dev.krud.crudframework.web.rest

import dev.krud.crudframework.crud.configuration.properties.CrudFrameworkProperties
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(CrudControllerProperties.CONFIGURATION_PREFIX)
class CrudControllerProperties {
    /**
     * The base URL for the CRUD Controller
     */
    var url = "/crud"
    companion object {
        const val CONFIGURATION_PREFIX = "${CrudFrameworkProperties.CONFIGURATION_PREFIX}.rest"
    }
}