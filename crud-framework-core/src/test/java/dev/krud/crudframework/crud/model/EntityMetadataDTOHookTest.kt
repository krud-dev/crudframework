package dev.krud.crudframework.crud.model

import dev.krud.crudframework.crud.annotation.WithHooks
import dev.krud.crudframework.crud.hooks.interfaces.CRUDHooks
import dev.krud.crudframework.crud.test.AbstractTestEntity
import dev.krud.crudframework.model.BaseCrudEntity
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import java.io.Serializable

class EntityMetadataDTOHookTest {
    private class GenericPersistentHooks<ID : Serializable> :
        CRUDHooks<ID, BaseCrudEntity<ID>>
    private class GenericPersistentHooks2<ID : Serializable> :
        CRUDHooks<ID, BaseCrudEntity<ID>>

    @Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
    @WithHooks(hooks = [GenericPersistentHooks::class])
    private annotation class WithGenericPersistentHooks

    @Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
    @WithHooks(hooks = [GenericPersistentHooks2::class])
    private annotation class WithGenericPersistentHooks2

    @Test
    fun `single nested withHooks annotation should register`() {
        @WithGenericPersistentHooks
        class TestEntity : AbstractTestEntity()

        val metadata = EntityMetadataDTO(TestEntity::class.java)

        assertArrayEquals(arrayOf(GenericPersistentHooks::class.java), metadata.hookTypesFromAnnotations.toTypedArray())
    }

    @Test
    fun `two nested withHooks annotation should register`() {
        @WithGenericPersistentHooks
        @WithGenericPersistentHooks2
        class TestEntity : AbstractTestEntity()

        val metadata = EntityMetadataDTO(TestEntity::class.java)

        assertArrayEquals(arrayOf(GenericPersistentHooks::class.java, GenericPersistentHooks2::class.java), metadata.hookTypesFromAnnotations.toTypedArray())
    }

    @Test
    fun `two nested withHooks annotation should register with one on parent`() {
        @WithGenericPersistentHooks2 abstract class AbstractAnnotatedTestEntity : AbstractTestEntity()

        @WithGenericPersistentHooks class TestEntity : AbstractAnnotatedTestEntity()

        val metadata = EntityMetadataDTO(TestEntity::class.java)

        assertArrayEquals(arrayOf(GenericPersistentHooks::class.java, GenericPersistentHooks2::class.java), metadata.hookTypesFromAnnotations.toTypedArray())
    }

    @Test
    fun `one withHooks annotation with multiple hooks should register`() {
        @WithHooks(hooks = [GenericPersistentHooks::class, GenericPersistentHooks2::class])
        class TestEntity : AbstractTestEntity()

        val metadata = EntityMetadataDTO(TestEntity::class.java)

        assertArrayEquals(arrayOf(GenericPersistentHooks::class.java, GenericPersistentHooks2::class.java), metadata.hookTypesFromAnnotations.toTypedArray())
    }

    @Test
    fun `nested witHooks annotation with multiple hooks should register`() {
        @WithHooks(hooks = [GenericPersistentHooks::class, GenericPersistentHooks2::class])
        abstract class AbstractAnnotatedTestEntity : AbstractTestEntity()

        class TestEntity : AbstractAnnotatedTestEntity()

        val metadata = EntityMetadataDTO(TestEntity::class.java)

        assertArrayEquals(arrayOf(GenericPersistentHooks::class.java, GenericPersistentHooks2::class.java), metadata.hookTypesFromAnnotations.toTypedArray())
    }

    @Test
    fun `withHooks should apply on field`() {
        class TestEntity : AbstractTestEntity() {
            @WithHooks(hooks = [GenericPersistentHooks::class])
            val someField: String = "ABCD"

            @WithGenericPersistentHooks2
            val someOtherField: String = "ABCD"
        }

        val metadata = EntityMetadataDTO(TestEntity::class.java)
        assertArrayEquals(arrayOf(GenericPersistentHooks::class.java, GenericPersistentHooks2::class.java), metadata.hookTypesFromAnnotations.toTypedArray())
    }
}