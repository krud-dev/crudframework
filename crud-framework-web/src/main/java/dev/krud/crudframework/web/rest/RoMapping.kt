package dev.krud.crudframework.web.rest

import dev.krud.crudframework.ro.BaseRO
import kotlin.reflect.KClass

annotation class RoMapping(
        val mainRoClass: KClass<*> = BaseRO::class,
        val showRoClass: KClass<*> = BaseRO::class,
        val createRoClass: KClass<*> = BaseRO::class,
        val updateRoClass: KClass<*> = BaseRO::class,
        val indexRoClass: KClass<*> = BaseRO::class
)