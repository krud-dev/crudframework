package dev.krud.crudframework.crud.configuration

import dev.krud.crudframework.crud.handler.*
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import dev.krud.crudframework.crud.configuration.properties.CrudFrameworkProperties
import dev.krud.crudframework.crud.handler.*
import dev.krud.crudframework.crud.policy.Policy
import dev.krud.crudframework.crud.security.PrincipalProvider
import dev.krud.crudframework.exception.WrapExceptionAspect
import dev.krud.crudframework.model.PersistentEntity

@Configuration
@EnableConfigurationProperties(CrudFrameworkProperties::class)
class CrudFrameworkConfiguration {

    @Bean
    fun crudHandler(): CrudHandler =
        CrudHandlerImpl()

    @Bean
    fun crudHelper(): CrudHelper =
        CrudHelperImpl()

    @Bean
    fun crudCreateHandler(): CrudCreateHandler =
        CrudCreateHandlerImpl()

    @Bean
    fun crudDeleteHandler(): CrudDeleteHandler =
        CrudDeleteHandlerImpl()

    @Bean
    fun crudUpdateHandler(): CrudUpdateHandler =
        CrudUpdateHandlerImpl()

    @Bean
    fun crudReadHandler(): CrudReadHandler =
        CrudReadHandlerImpl()

    @Bean
    fun crudSecurityHandler(policies: ObjectProvider<Policy<PersistentEntity>>, principalProvider: ObjectProvider<PrincipalProvider>): CrudSecurityHandler {
        return CrudSecurityHandlerImpl(policies, principalProvider)
    }

    @Bean
    fun wrapExceptionAspect(): WrapExceptionAspect = WrapExceptionAspect()
}