package dev.krud.crudframework.jpa.dao

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import dev.krud.crudframework.modelfilter.FilterField
import dev.krud.crudframework.modelfilter.enums.FilterFieldDataType
import dev.krud.crudframework.modelfilter.enums.FilterFieldOperation
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.metamodel.Attribute
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class FilterFieldPredicateBuilderTest {
    private val criteriaBuilder = mock<CriteriaBuilder>()
    private val from = mock<From<Any, Any>>()

    @Test
    fun `builds predicate against any from, not only a query root`() {
        val path = mock<Path<Any>>()
        val predicate = mock<Predicate>()
        doReturn(path).`when`(from).get<Any>("status")
        doReturn(predicate).`when`(criteriaBuilder).equal(path, "ACTIVE")

        val result = FilterFieldPredicateBuilder.toPredicate(criteriaBuilder, FilterField("status", FilterFieldOperation.Equal, FilterFieldDataType.String, "ACTIVE"), from)

        assertSame(predicate, result)
    }

    @Test
    fun `nested path joins intermediate association on the given from`() {
        val join = mock<Join<Any, Any>>()
        val path = mock<Path<Any>>()
        doReturn(emptySet<Join<Any, *>>()).`when`(from).joins
        doReturn(join).`when`(from).join<Any, Any>("business")
        doReturn(path).`when`(join).get<Any>("salesRepId")

        val result = FilterFieldPredicateBuilder.resolveExpression(from, "business.salesRepId")

        assertSame(path, result)
    }

    @Test
    fun `nested path reuses an existing join for the same attribute`() {
        val attribute = mock<Attribute<Any, Any>>()
        doReturn("business").`when`(attribute).name
        val existingJoin = mock<Join<Any, Any>>()
        doReturn(attribute).`when`(existingJoin).attribute
        val path = mock<Path<Any>>()
        doReturn(setOf(existingJoin)).`when`(from).joins
        doReturn(path).`when`(existingJoin).get<Any>("salesRepId")

        val result = FilterFieldPredicateBuilder.resolveExpression(from, "business.salesRepId")

        assertSame(path, result)
        verify(from, never()).join<Any, Any>("business")
    }

    @Test
    fun `builds one predicate per filter field`() {
        val firstPath = mock<Path<Any>>()
        val secondPath = mock<Path<Any>>()
        val firstPredicate = mock<Predicate>()
        val secondPredicate = mock<Predicate>()
        doReturn(firstPath).`when`(from).get<Any>("a")
        doReturn(secondPath).`when`(from).get<Any>("b")
        doReturn(firstPredicate).`when`(criteriaBuilder).isNull(firstPath)
        doReturn(secondPredicate).`when`(criteriaBuilder).isNotNull(secondPath)

        val result = FilterFieldPredicateBuilder.toPredicates(
            criteriaBuilder,
            listOf(
                FilterField("a", FilterFieldOperation.IsNull, FilterFieldDataType.String),
                FilterField("b", FilterFieldOperation.IsNotNull, FilterFieldDataType.String)
            ),
            from
        )

        assertEquals(listOf(firstPredicate, secondPredicate), result)
    }
}
