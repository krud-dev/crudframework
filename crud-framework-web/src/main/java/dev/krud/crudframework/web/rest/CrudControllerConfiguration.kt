package dev.krud.crudframework.web.rest

import org.reflections.Reflections
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportAware
import org.springframework.core.type.AnnotationMetadata
import dev.krud.crudframework.crud.handler.CrudHandler
import dev.krud.crudframework.model.BaseCrudEntity
import kotlin.reflect.KClass

@Configuration
@EnableConfigurationProperties(CrudControllerProperties::class)
class CrudControllerConfiguration : ImportAware, BeanFactoryPostProcessor {
    private lateinit var packageName: String
    override fun setImportMetadata(importMetadata: AnnotationMetadata) {
        packageName = importMetadata.className.substringBeforeLast(".")
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        Reflections(packageName).getTypesAnnotatedWith(CrudController::class.java).forEach {
            val crudController = it.getAnnotation(CrudController::class.java)
            val definition = CrudControllerDefinition(crudController, it.kotlin as KClass<BaseCrudEntity<*>>)
            beanFactory.registerSingleton("${it.simpleName}CrudControllerDefinition", definition)
        }
    }

    @Bean
    fun crudRestService(crudHandler: CrudHandler, @Autowired(required = false) crudControllerDefinitions: List<CrudControllerDefinition>?): CrudRestService {
        return CrudRestServiceImpl(crudHandler, crudControllerDefinitions ?: emptyList())
    }
}