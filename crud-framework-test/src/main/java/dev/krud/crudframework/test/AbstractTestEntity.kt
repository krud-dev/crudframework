package dev.krud.crudframework.test

import dev.krud.shapeshift.resolver.annotation.MappedField
import dev.krud.crudframework.crud.annotation.CrudEntity
import dev.krud.crudframework.model.BaseCrudEntity

@CrudEntity(dao = TestCrudDaoImpl::class)
abstract class AbstractTestEntity(@MappedField(target = AbstractTestRO::class) override var id: Long) : BaseCrudEntity<Long>() {
    override fun exists(): Boolean {
        return id != 0L
    }
}