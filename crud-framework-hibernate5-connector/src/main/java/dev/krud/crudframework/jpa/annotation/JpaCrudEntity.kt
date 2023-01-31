package dev.krud.crudframework.jpa.annotation

import dev.krud.crudframework.crud.annotation.CrudEntity
import dev.krud.crudframework.jpa.dao.JpaDaoImpl

@CrudEntity(dao = JpaDaoImpl::class)
annotation class JpaCrudEntity