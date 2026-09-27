package dev.krud.crudframework.jpa.dao

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.FilterField
import dev.krud.crudframework.modelfilter.enums.FilterFieldDataType
import dev.krud.crudframework.modelfilter.enums.FilterFieldOperation
import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.persistence.metamodel.Attribute
import jakarta.persistence.metamodel.PluralAttribute
import jakarta.persistence.metamodel.Type
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JpaDaoImplTest {
    private val predicate = mock<Predicate>()
    private val path = mock<Path<Any>>()
    private val root = mock<Root<Any>>()
    private val criteriaQuery = mock<CriteriaQuery<Any>>()
    private val criteriaBuilder = mock<CriteriaBuilder>()
    private val typedQuery = mock<TypedQuery<Any>>()
    private val entityManager = mock<EntityManager>()
    private val subject = JpaDaoImpl()

    @BeforeEach
    fun setUp() {
        doReturn(criteriaBuilder).`when`(entityManager).criteriaBuilder
        doReturn(criteriaQuery).`when`(criteriaBuilder).createQuery()
        doReturn(root).`when`(criteriaQuery).from(any<Class<*>>())
        doReturn(path).`when`(root).get<Any>("events")
        doReturn(predicate).`when`(criteriaBuilder).isMember(any<Any>(), any<Expression<Collection<Any>>>())
        doReturn(typedQuery).`when`(entityManager).createQuery(criteriaQuery)
        doReturn(mutableListOf<Any>()).`when`(typedQuery).resultList
        JpaDaoImpl::class.java.getDeclaredField("entityManager").apply {
            isAccessible = true
            set(subject, entityManager)
        }
    }

    @Test
    fun `contains on enum element collection with string value uses member of with enum value`() {
        mockModel(Attribute.PersistentAttributeType.ELEMENT_COLLECTION, TestEvent::class.java)

        subject.index(filterOf(FilterField("events", FilterFieldOperation.Contains, FilterFieldDataType.Object, "Created")), TestEntity::class.java)

        verify(criteriaBuilder).isMember(eq<Any>(TestEvent.Created), eq(path as Expression<Collection<Any>>))
        verify(criteriaBuilder, never()).function(any(), any<Class<*>>(), any())
    }

    @Test
    fun `contains on enum element collection with enum value uses member of with enum value`() {
        mockModel(Attribute.PersistentAttributeType.ELEMENT_COLLECTION, TestEvent::class.java)

        subject.index(filterOf(FilterField("events", FilterFieldOperation.Contains, TestEvent::class.java.name, "Updated")), TestEntity::class.java)

        verify(criteriaBuilder).isMember(eq<Any>(TestEvent.Updated), eq(path as Expression<Collection<Any>>))
    }

    @Test
    fun `contains on string element collection uses member of with raw value`() {
        mockModel(Attribute.PersistentAttributeType.ELEMENT_COLLECTION, String::class.java)

        subject.index(filterOf(FilterField("events", FilterFieldOperation.Contains, FilterFieldDataType.String, "x")), TestEntity::class.java)

        verify(criteriaBuilder).isMember(eq<Any>("x"), eq(path as Expression<Collection<Any>>))
    }

    private fun mockModel(persistentAttributeType: Attribute.PersistentAttributeType, elementJavaType: Class<*>) {
        val elementType = mock<Type<Any>>()
        doReturn(elementJavaType).`when`(elementType).javaType
        val attribute = mock<PluralAttribute<Any, Collection<Any>, Any>>()
        doReturn(persistentAttributeType).`when`(attribute).persistentAttributeType
        doReturn(elementType).`when`(attribute).elementType
        doReturn(attribute).`when`(path).model
        doReturn(List::class.java).`when`(path).javaType
    }

    private fun filterOf(filterField: FilterField) = DynamicModelFilter().add(filterField)

    enum class TestEvent {
        Created,
        Updated
    }

    class TestEntity : BaseCrudEntity<Long>() {
        override var id: Long = 0L
        override fun exists(): Boolean = false
    }
}
