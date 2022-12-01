package dev.krud.crudframework.mongo.annotation

import dev.krud.crudframework.crud.annotation.CrudEntity
import dev.krud.crudframework.mongo.dao.MongoCrudDaoImpl

@CrudEntity(dao = MongoCrudDaoImpl::class)
annotation class MongoCrudEntity