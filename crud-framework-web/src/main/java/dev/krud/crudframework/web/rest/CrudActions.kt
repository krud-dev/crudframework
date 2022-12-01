package dev.krud.crudframework.web.rest

annotation class CrudActions(
        val create: Boolean = true,
        val delete: Boolean = true,
        val update: Boolean = true,
        val show: Boolean = true,
        val index: Boolean = true
)