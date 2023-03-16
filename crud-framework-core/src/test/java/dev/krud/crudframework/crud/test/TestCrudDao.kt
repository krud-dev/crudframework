package dev.krud.crudframework.crud.test

import dev.krud.crudframework.crud.handler.CrudDao
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import java.io.Serializable

class TestCrudDao : CrudDao {
    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?, E : DynamicModelFilter?> index(filter: E, clazz: Class<Entity>?): MutableList<Entity> {
        TODO("Not yet implemented")
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?, E : DynamicModelFilter?> indexCount(filter: E, clazz: Class<Entity>?): Long {
        TODO("Not yet implemented")
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> hardDeleteById(id: ID, clazz: Class<Entity>?) {
        TODO("Not yet implemented")
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> saveOrUpdate(entity: Entity): Entity {
        TODO("Not yet implemented")
    }

    override fun <ID : Serializable?, Entity : BaseCrudEntity<ID>?> saveOrUpdate(entities: MutableList<Entity>?): MutableList<Entity> {
        TODO("Not yet implemented")
    }
}