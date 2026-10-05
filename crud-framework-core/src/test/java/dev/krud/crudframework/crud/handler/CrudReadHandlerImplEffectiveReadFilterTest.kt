package dev.krud.crudframework.crud.handler

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import dev.krud.crudframework.crud.exception.CrudReadException
import dev.krud.crudframework.crud.hooks.interfaces.IndexHooks
import dev.krud.crudframework.crud.policy.PolicyRuleType
import dev.krud.crudframework.crud.test.AbstractTestEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.FilterFields
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CrudReadHandlerImplEffectiveReadFilterTest {
    class TestEntity : AbstractTestEntity()

    private val crudHelper = mock<CrudHelper>()
    private val crudSecurityHandler = mock<CrudSecurityHandler>()
    private val firstHook = mock<IndexHooks<Long, TestEntity>>()
    private val secondHook = mock<IndexHooks<Long, TestEntity>>()
    private val subject = CrudReadHandlerImpl()

    private val userField = FilterFields.eq("name", "x")
    private val policyField = FilterFields.eq("isTest", "false")
    private val hookField = FilterFields.eq("hooked", "true")
    private val softDeleteField = FilterFields.eq("deleted", "false")

    @BeforeEach
    fun setUp() {
        inject("crudHelper", crudHelper)
        inject("crudSecurityHandler", crudSecurityHandler)

        doReturn(listOf(firstHook, secondHook)).`when`(crudHelper).getHooks<Long, TestEntity, IndexHooks<*, *>>(eq(IndexHooks::class.java), eq(TestEntity::class.java))
        doAnswer { (it.arguments[1] as DynamicModelFilter).add(policyField); null }
            .`when`(crudSecurityHandler).decorateFilter(eq(TestEntity::class.java), any())
        doAnswer { (it.arguments[0] as DynamicModelFilter).add(hookField); null }
            .`when`(secondHook).preIndex(any())
        doAnswer { (it.arguments[0] as DynamicModelFilter).add(softDeleteField); null }
            .`when`(crudHelper).decorateFilter<Long, TestEntity>(any(), eq(TestEntity::class.java))
    }

    @Test
    fun `adds policy, hook and soft delete filter fields to a copy of the filter`() {
        val filter = DynamicModelFilter().add(userField)

        val result = subject.buildEffectiveReadFilter<Long, TestEntity>(filter, TestEntity::class.java)

        assertEquals(listOf(userField, policyField, hookField, softDeleteField), result.filterFields)
        assertEquals(listOf(userField), filter.filterFields)
    }

    @Test
    fun `runs the same steps in the same order as an index read`() {
        val result = subject.buildEffectiveReadFilter<Long, TestEntity>(DynamicModelFilter(), TestEntity::class.java)

        inOrder(crudSecurityHandler, crudHelper, firstHook, secondHook) {
            verify(crudSecurityHandler).evaluatePreRulesAndThrow(PolicyRuleType.CAN_ACCESS, TestEntity::class.java)
            verify(crudSecurityHandler).decorateFilter(TestEntity::class.java, result)
            verify(crudHelper).validateAndFillFilterFieldMetadata<Long, TestEntity>(result.filterFields, TestEntity::class.java)
            verify(secondHook).preIndex(result)
            verify(firstHook).preIndex(result)
            verify(crudHelper).decorateFilter<Long, TestEntity>(result, TestEntity::class.java)
        }
    }

    @Test
    fun `null filter yields only the added filter fields`() {
        val result = subject.buildEffectiveReadFilter<Long, TestEntity>(null, TestEntity::class.java)

        assertEquals(listOf(policyField, hookField, softDeleteField), result.filterFields)
    }

    @Test
    fun `refuses entities whose access policies have post conditions`() {
        doReturn(true).`when`(crudSecurityHandler).hasPostRules(PolicyRuleType.CAN_ACCESS, TestEntity::class.java)

        assertThrows<CrudReadException> {
            subject.buildEffectiveReadFilter<Long, TestEntity>(DynamicModelFilter(), TestEntity::class.java)
        }
        verify(crudSecurityHandler, never()).decorateFilter(any(), any())
    }

    @Test
    fun `pre rule failure is thrown before the filter is decorated`() {
        doThrow(RuntimeException("denied")).`when`(crudSecurityHandler).evaluatePreRulesAndThrow(PolicyRuleType.CAN_ACCESS, TestEntity::class.java)

        assertThrows<RuntimeException> {
            subject.buildEffectiveReadFilter<Long, TestEntity>(DynamicModelFilter(), TestEntity::class.java)
        }
        verify(crudSecurityHandler, never()).decorateFilter(any(), any())
    }

    private fun inject(fieldName: String, value: Any) {
        CrudReadHandlerImpl::class.java.getDeclaredField(fieldName).apply {
            isAccessible = true
            set(subject, value)
        }
    }
}
