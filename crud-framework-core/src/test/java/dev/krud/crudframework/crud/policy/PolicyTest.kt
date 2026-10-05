package dev.krud.crudframework.crud.policy

import dev.krud.crudframework.crud.test.AbstractTestEntity
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PolicyTest {
    class TestEntity : AbstractTestEntity()

    @Test
    fun `policy without rules has no post conditions`() {
        val subject = policy<TestEntity> {
            filter { TestEntity::id Equal 1L }
        }

        PolicyRuleType.values().forEach { assertFalse(subject.hasPostConditions(it)) }
    }

    @Test
    fun `pre conditions only are not post conditions`() {
        val subject = policy<TestEntity> {
            canAccess { preCondition { true } }
            canUpdate { preCondition { true } }
        }

        PolicyRuleType.values().forEach { assertFalse(subject.hasPostConditions(it)) }
    }

    @Test
    fun `access post condition applies to access, update and delete but not create`() {
        val subject = policy<TestEntity> {
            canAccess { postCondition { _, _ -> true } }
        }

        assertTrue(subject.hasPostConditions(PolicyRuleType.CAN_ACCESS))
        assertTrue(subject.hasPostConditions(PolicyRuleType.CAN_UPDATE))
        assertTrue(subject.hasPostConditions(PolicyRuleType.CAN_DELETE))
        assertFalse(subject.hasPostConditions(PolicyRuleType.CAN_CREATE))
    }

    @Test
    fun `update post condition does not apply to access`() {
        val subject = policy<TestEntity> {
            canUpdate { postCondition { _, _ -> true } }
        }

        assertFalse(subject.hasPostConditions(PolicyRuleType.CAN_ACCESS))
        assertTrue(subject.hasPostConditions(PolicyRuleType.CAN_UPDATE))
        assertFalse(subject.hasPostConditions(PolicyRuleType.CAN_DELETE))
    }
}
