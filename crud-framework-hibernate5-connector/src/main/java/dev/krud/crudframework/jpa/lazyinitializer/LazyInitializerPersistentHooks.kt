package dev.krud.crudframework.jpa.lazyinitializer

import dev.krud.crudframework.crud.hooks.interfaces.CreateFromHooks
import dev.krud.crudframework.crud.hooks.interfaces.CreateHooks
import dev.krud.crudframework.crud.hooks.interfaces.IndexHooks
import dev.krud.crudframework.crud.hooks.interfaces.ShowByHooks
import dev.krud.crudframework.crud.hooks.interfaces.ShowHooks
import dev.krud.crudframework.crud.hooks.interfaces.UpdateFromHooks
import dev.krud.crudframework.crud.hooks.interfaces.UpdateHooks
import dev.krud.crudframework.jpa.lazyinitializer.annotation.InitializeLazyOn
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.ro.PagedResult
import dev.krud.crudframework.util.ReflectionUtils
import org.hibernate.Hibernate

class LazyInitializerPersistentHooks :
    ShowHooks<Long, BaseCrudEntity<Long>>,
    ShowByHooks<Long, BaseCrudEntity<Long>>,
    IndexHooks<Long, BaseCrudEntity<Long>>,
    UpdateHooks<Long, BaseCrudEntity<Long>>,
    UpdateFromHooks<Long, BaseCrudEntity<Long>>,
    CreateHooks<Long, BaseCrudEntity<Long>>,
    CreateFromHooks<Long, BaseCrudEntity<Long>> {

    override fun onShow(entity: BaseCrudEntity<Long>?) {
        entity ?: return
        initializeLazyFields(entity) { it.show }
    }

    override fun onCreateFrom(entity: BaseCrudEntity<Long>, ro: Any) {
        initializeLazyFields(entity) { it.createFrom }
    }

    override fun onCreate(entity: BaseCrudEntity<Long>) {
        initializeLazyFields(entity) { it.create }
    }

    override fun onIndex(filter: DynamicModelFilter, result: PagedResult<BaseCrudEntity<Long>>) {
        result.results ?: return
        for (entity in result.results) {
            initializeLazyFields(entity) { it.index }
        }
    }

    override fun onShowBy(entity: BaseCrudEntity<Long>?) {
        entity ?: return
        initializeLazyFields(entity) { it.showBy }
    }

    override fun onUpdateFrom(entity: BaseCrudEntity<Long>, ro: Any) {
        initializeLazyFields(entity) { it.updateFrom }
    }

    override fun onUpdate(entity: BaseCrudEntity<Long>) {
        initializeLazyFields(entity) { it.update }
    }

    private fun initializeLazyFields(entity: BaseCrudEntity<Long>, condition: (annotation: InitializeLazyOn) -> Boolean) {
        ReflectionUtils.doWithFields(entity::class.java) {
            val annotation = it.getDeclaredAnnotation(ANNOTATION_TYPE) ?: return@doWithFields
            if (condition(annotation)) {
                ReflectionUtils.makeAccessible(it)
                Hibernate.initialize(it.get(entity))
            }
        }
    }

    companion object {
        private val ANNOTATION_TYPE = InitializeLazyOn::class.java
    }
}