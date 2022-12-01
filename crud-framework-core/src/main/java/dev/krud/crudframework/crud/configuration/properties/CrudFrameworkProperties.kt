package dev.krud.crudframework.crud.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(CrudFrameworkProperties.CONFIGURATION_PREFIX)
class CrudFrameworkProperties {
    companion object {
        const val CONFIGURATION_PREFIX = "crud"
    }
}