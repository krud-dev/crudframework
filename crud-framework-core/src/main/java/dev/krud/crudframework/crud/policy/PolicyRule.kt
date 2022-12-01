package dev.krud.crudframework.crud.policy

import dev.krud.crudframework.model.PersistentEntity
import java.security.Principal

class PolicyRule<RootType : PersistentEntity>(
    val name: String,
    val location: PolicyElementLocation,
    val type: PolicyRuleType,
    val preConditions: List<PolicyPreCondition>,
    val postConditions: List<PolicyPostCondition<RootType>>
) {
    fun evaluatePreConditions(principal: Principal?): Result<RootType> {
        val conditionResults = preConditions.map {
            val result = it.supplier(principal)
            PolicyPreCondition.Result(result, it)
        }
        return Result(
            conditionResults.all { it.success },
            this,
            conditionResults
        )
    }

    fun evaluatePostConditions(entity: RootType, principal: Principal?): Result<RootType> {
        val postConditionResults = postConditions.map {
            val result = it.supplier(entity, principal)
            PolicyPostCondition.Result(result, it)
        }
        return Result(
            postConditionResults.all { it.success },
            this,
            emptyList(),
            postConditionResults
        )
    }

    data class Result<RootType : PersistentEntity>(
        val success: Boolean,
        val rule: PolicyRule<RootType>,
        val preConditionResults: List<PolicyPreCondition.Result>,
        val postConditionResults: List<PolicyPostCondition.Result<RootType>> = listOf()
    )

    companion object {
        fun <RootType : PersistentEntity> Collection<PolicyRule<RootType>>.evaluatePreConditions(principal: Principal?): List<PolicyRule.Result<RootType>> {
            return this.map { it.evaluatePreConditions(principal) }
        }

        fun <RootType : PersistentEntity> Collection<PolicyRule<RootType>>.evaluatePostConditions(
            entity: RootType,
            principal: Principal?
        ): List<PolicyRule.Result<RootType>> {
            return this.map { it.evaluatePostConditions(entity, principal) }
        }
    }
}