package dev.krud.crudframework.test

import dev.krud.crudframework.crud.handler.CrudDao
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.util.filtersMatch
import java.io.Serializable

class TestCrudDaoImpl : CrudDao {
    val entities = mutableListOf<BaseCrudEntity<out Serializable>>()
    override fun <ID : Serializable, Entity : BaseCrudEntity<ID>, E : DynamicModelFilter> index(filter: E, clazz: Class<Entity>?): MutableList<Entity> {
        return entities.filter {
            filter.filtersMatch(it)
        } as MutableList<Entity>
    }

    override fun <ID : Serializable, Entity : BaseCrudEntity<ID>, E : DynamicModelFilter> indexCount(filter: E, clazz: Class<Entity>?): Long {
        return entities.filter {
            filter.filtersMatch(it)
        }.size.toLong()
    }

    override fun <ID : Serializable, Entity : BaseCrudEntity<ID>> hardDeleteById(id: ID, clazz: Class<Entity>?) {
        entities.removeIf {
            it.id == id
        }
    }

    override fun <ID : Serializable, Entity : BaseCrudEntity<ID>> saveOrUpdate(entity: Entity): Entity {
        entities.removeIf {
            it.id == entity.id
        }
        entities.add(entity)
        return entity
    }

    override fun <ID : Serializable, Entity : BaseCrudEntity<ID>> saveOrUpdate(entities: List<Entity>): List<Entity> {
        entities.forEach {
            this.entities.removeIf { entity ->
                entity.id == it.id
            }
            this.entities.add(it)
        }
        return entities
    }
}