package dev.krud.crudframework.crud.test

import dev.krud.crudframework.crud.annotation.CrudEntity
import dev.krud.crudframework.model.BaseCrudEntity

@CrudEntity(TestCrudDao::class)
abstract class AbstractTestEntity : BaseCrudEntity<Long>() {
    override var id: Long
        get() = 0L
        set(value) {}

    override fun exists(): Boolean {
        return false
    }
}