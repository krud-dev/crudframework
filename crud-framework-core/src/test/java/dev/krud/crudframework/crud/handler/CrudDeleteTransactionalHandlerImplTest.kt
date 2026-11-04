package dev.krud.crudframework.crud.handler

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.krud.crudframework.crud.hooks.delete.CRUDOnDeleteHook
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.all
import strikt.assertions.containsExactly
import strikt.assertions.isTrue

class DeletableTestEntity(override var id: Long) : BaseCrudEntity<Long>() {
    var deleted: Boolean = false

    override fun exists(): Boolean = true
}

class CrudDeleteTransactionalHandlerImplTest {

    private val crudDao = mock<CrudDao>()
    private val crudHelper = mock<CrudHelper> {
        on { getCrudDaoForEntity(DeletableTestEntity::class.java) } doReturn crudDao
    }
    private val handler = CrudDeleteTransactionalHandlerImpl(crudHelper, mock())
    private val deleteField = DeletableTestEntity::class.java.getDeclaredField("deleted")
    private val filter = DynamicModelFilter()

    private val entities = listOf(DeletableTestEntity(1), DeletableTestEntity(2), DeletableTestEntity(3))
    private val hookedEntities = mutableListOf<DeletableTestEntity>()
    private val onHooks = listOf(CRUDOnDeleteHook<Long, DeletableTestEntity> { hookedEntities.add(it) })

    init {
        whenever(crudHelper.getEntities(filter, DeletableTestEntity::class.java, null)).thenReturn(entities)
    }

    @Test
    fun `bulk soft delete deletes each loaded entity without re-fetching by the bulk filter`() {
        val result = handler.bulkDeleteSoftTransactional(filter, deleteField, DeletableTestEntity::class.java, onHooks, false)

        expectThat(result).containsExactly(entities)
        expectThat(entities).all { get { deleted }.isTrue() }
        expectThat(hookedEntities).containsExactly(entities)
        verify(crudHelper, times(1)).getEntities(any(), any<Class<DeletableTestEntity>>(), anyOrNull())
        verify(crudHelper, never()).getEntity(any(), any<Class<DeletableTestEntity>>(), anyOrNull())
    }

    @Test
    fun `bulk hard delete deletes each loaded entity by its own id without re-fetching by the bulk filter`() {
        val result = handler.bulkDeleteHardTransactional(filter, DeletableTestEntity::class.java, onHooks, false)

        expectThat(result).containsExactly(entities)
        expectThat(hookedEntities).containsExactly(entities)
        entities.forEach { verify(crudDao).hardDeleteById(it.id, DeletableTestEntity::class.java) }
        verify(crudHelper, times(1)).getEntities(any(), any<Class<DeletableTestEntity>>(), anyOrNull())
        verify(crudHelper, never()).getEntity(any(), any<Class<DeletableTestEntity>>(), anyOrNull())
    }
}
