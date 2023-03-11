package dev.krud.crudframework.crud.handler.krud

import dev.krud.crudframework.model.BaseCrudEntity
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.AutowireCandidateQualifier
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.ResolvableType
import org.springframework.core.env.Environment
import org.springframework.core.type.AnnotationMetadata
import java.util.Locale

class KrudScanner(private val environment: Environment) : ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(importingClassMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        val basePackages = resolveBasePackages(importingClassMetadata)
        if (basePackages.isEmpty()) {
            return
        }

        val scanner = ClassPathScanningCandidateComponentProvider(false, environment)
        scanner.addIncludeFilter { metadata, _ ->
            val clazz = Class.forName(metadata.classMetadata.className)
            BaseCrudEntity::class.java.isAssignableFrom(clazz)
        }
        basePackages.forEach { basePackage ->
            scanner.findCandidateComponents(basePackage).forEach {
                registerBeanDefinition(it, registry)
            }
        }
    }

    private fun registerBeanDefinition(
            it: BeanDefinition, registry: BeanDefinitionRegistry) {
        val entityClazz = Class.forName(it.beanClassName)
        if (!BaseCrudEntity::class.java.isAssignableFrom(entityClazz)) {
            throw RuntimeException("Class ${entityClazz.simpleName} must extend BaseCrudEntity")
        }

        val type = ResolvableType.forClassWithGenerics(
            Krud::class.java, entityClazz, box(
                entityClazz.getMethod("getId").returnType
            )
        )
        val beanDefinition = RootBeanDefinition(KrudImpl::class.java)
        beanDefinition.setTargetType(type)
        beanDefinition.scope = BeanDefinition.SCOPE_SINGLETON
        beanDefinition.propertyValues.add("entityClazz", entityClazz)
        beanDefinition.addQualifier(
            AutowireCandidateQualifier(
                Qualifier::class.java, entityClazz.name
            )
        )
        registry.registerBeanDefinition(
            registry.generateBeanName(
                entityClazz
            ), beanDefinition
        )
    }

    private fun resolveBasePackages(importingClassMetadata: AnnotationMetadata): List<String> {
        val attributes = importingClassMetadata.getAnnotationAttributes(EnableKrud::class.java.name)
        return if (attributes != null) {
            val basePackages = attributes["value"] as Array<String>
            if (basePackages.isEmpty()) {
                val packageName = importingClassMetadata.className.substringBeforeLast(".")
                listOf(packageName)
            } else {
                basePackages.toList()
            }
        } else {
            emptyList()
        }
    }

    private fun BeanDefinitionRegistry.generateBeanName(clazz: Class<out Any>): String {
        val originalCandidate = clazz.simpleName.replaceFirstChar { it.lowercase(Locale.getDefault()) } + "Krud"
        var candidate = originalCandidate
        var i = 2
        while (containsBeanDefinition(candidate)) {
            candidate = originalCandidate + i++
        }
        return candidate
    }

    private fun box(value: Class<*>): Class<*> {
        if (value.isPrimitive) {
            when (value) {
                Boolean::class.javaPrimitiveType -> return java.lang.Boolean::class.java
                Byte::class.javaPrimitiveType -> return java.lang.Byte::class.java
                Short::class.javaPrimitiveType -> return java.lang.Short::class.java
                Int::class.javaPrimitiveType -> return java.lang.Integer::class.java
                Long::class.javaPrimitiveType -> return java.lang.Long::class.java
                Float::class.javaPrimitiveType -> return java.lang.Float::class.java
                Double::class.javaPrimitiveType -> return java.lang.Double::class.java
                Char::class.javaPrimitiveType -> return java.lang.Character::class.java
            }
        }
        return value
    }
}