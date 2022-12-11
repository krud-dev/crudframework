package dev.krud.crudframework.web.rest

import dev.krud.crudframework.ro.BaseRO
import kotlin.reflect.KClass

fun CrudControllerDefinition.effectiveMainRoClass(): KClass<out BaseRO<*>> {
    return if (annotation.roMapping.mainRoClass != BaseRO::class) {
        annotation.roMapping.mainRoClass as KClass<out BaseRO<*>>
    } else {
        throw IllegalArgumentException("No roMapping.mainRoClass specified for resource ${annotation.resourceName}")
    }
}

fun CrudControllerDefinition.effectiveShowRoClass(): KClass<out BaseRO<*>> {
    return if (annotation.roMapping.showRoClass != BaseRO::class) {
        annotation.roMapping.showRoClass as KClass<out BaseRO<*>>
    } else {
        effectiveMainRoClass()
    }
}

fun CrudControllerDefinition.effectiveIndexRoClass(): KClass<out BaseRO<*>> {
    return if (annotation.roMapping.indexRoClass != BaseRO::class) {
        annotation.roMapping.indexRoClass as KClass<out BaseRO<*>>
    } else {
        effectiveMainRoClass()
    }
}

fun CrudControllerDefinition.effectiveUpdateRoClass(): KClass<out BaseRO<*>> {
    return if (annotation.roMapping.updateRoClass != BaseRO::class) {
        annotation.roMapping.updateRoClass as KClass<out BaseRO<*>>
    } else {
        effectiveMainRoClass()
    }
}

fun CrudControllerDefinition.effectiveCreateRoClass(): KClass<out BaseRO<*>> {
    return if (annotation.roMapping.createRoClass != BaseRO::class) {
        annotation.roMapping.createRoClass as KClass<out BaseRO<*>>
    } else {
        effectiveMainRoClass()
    }
}