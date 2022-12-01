package dev.krud.crudframework.jpa.annotation

import dev.krud.crudframework.crud.annotation.CrudEntity
import dev.krud.crudframework.jpa.dao.CrudDaoImpl

@CrudEntity(dao = CrudDaoImpl::class)
annotation class JpaCrudEntity