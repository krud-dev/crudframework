package dev.krud.crudframework.crud.configuration

import dev.krud.crudframework.crud.cache.CacheManagerAdapter
import dev.krud.crudframework.crud.configuration.properties.CrudFrameworkProperties
import dev.krud.crudframework.crud.handler.*
import dev.krud.crudframework.crud.policy.Policy
import dev.krud.crudframework.crud.security.PrincipalProvider
import dev.krud.crudframework.exception.WrapExceptionAspect
import dev.krud.crudframework.model.PersistentEntity
import dev.krud.shapeshift.ShapeShift
import dev.krud.shapeshift.ShapeShiftBuilder
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(CrudFrameworkProperties::class)
class CrudFrameworkConfiguration {

    @Bean
    fun crudHandler(): CrudHandler =
        CrudHandlerImpl()

    @Bean
    fun crudHelper(
        @Autowired(required = false) crudDaos: List<CrudDao>,
        applicationContext: ApplicationContext,
        cacheManagerAdapter: CacheManagerAdapter,
        @Autowired(required = false) shapeShift: ShapeShift?
    ): CrudHelper =
        CrudHelperImpl(
            crudDaos,
            applicationContext,
            cacheManagerAdapter,
            shapeShift ?: ShapeShiftBuilder().build()
        )

    @Bean
    fun crudCreateHandler(): CrudCreateHandler =
        CrudCreateHandlerImpl()

    @Bean
    fun crudCreateTransactionalHandler(crudHelper: CrudHelper, crudSecurityHandler: CrudSecurityHandler): CrudCreateTransactionalHandler =
        CrudCreateTransactionalHandlerImpl(crudHelper, crudSecurityHandler)

    @Bean
    fun crudDeleteHandler(): CrudDeleteHandler =
        CrudDeleteHandlerImpl()

    @Bean
    fun crudDeleteTransactionalHandler(crudHelper: CrudHelper, crudSecurityHandler: CrudSecurityHandler): CrudDeleteTransactionalHandler =
        CrudDeleteTransactionalHandlerImpl(crudHelper, crudSecurityHandler)

    @Bean
    fun crudUpdateHandler(): CrudUpdateHandler =
        CrudUpdateHandlerImpl()

    @Bean
    fun crudUpdateTransactionalHandler(crudHelper: CrudHelper, crudSecurityHandler: CrudSecurityHandler): CrudUpdateTransactionalHandler =
        CrudUpdateTransactionalHandlerImpl(crudHelper, crudSecurityHandler)

    @Bean
    fun crudReadHandler(): CrudReadHandler =
        CrudReadHandlerImpl()

    @Bean
    fun crudReadTransactionalHandler(crudHelper: CrudHelper, crudSecurityHandler: CrudSecurityHandler): CrudReadTransactionalHandler =
        CrudReadTransactionalHandlerImpl(crudHelper, crudSecurityHandler)

    @Bean
    fun crudSecurityHandler(
        policies: ObjectProvider<Policy<PersistentEntity>>,
        principalProvider: ObjectProvider<PrincipalProvider>
    ): CrudSecurityHandler {
        return CrudSecurityHandlerImpl(policies, principalProvider)
    }

    @Bean
    fun wrapExceptionAspect(): WrapExceptionAspect = WrapExceptionAspect()
}