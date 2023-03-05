package dev.krud.crudframework.crud.policy

import dev.krud.crudframework.crud.policy.PolicyElementLocation.Companion.toPolicyElementLocation
import dev.krud.crudframework.model.PersistentEntity

class PolicyRuleBuilder<RootType : PersistentEntity>(private val name: String?, private val type: PolicyRuleType, private val location: PolicyElementLocation) {
    private val preConditions = mutableListOf<PolicyPreCondition>()
    private val postConditions = mutableListOf<PolicyPostCondition<RootType>>()

    fun preCondition(name: String? = null, supplier: PolicyPreConditionSupplier) {
        this.preConditions += PolicyPreCondition(name ?: DEFAULT_PRE_CONDITION_NAME, Thread.currentThread().stackTrace[2].toPolicyElementLocation(), supplier)
    }

    fun postCondition(name: String? = null, supplier: PolicyPostConditionSupplier<RootType>) {
        this.postConditions += PolicyPostCondition(name ?: DEFAULT_POST_CONDITION_NAME, Thread.currentThread().stackTrace[2].toPolicyElementLocation(), supplier)
    }

    fun build(): PolicyRule<RootType> {
        return PolicyRule(name ?: DEFAULT_RULE_NAME, location, type, preConditions, postConditions)
    }

    companion object {
        const val DEFAULT_RULE_NAME = "unnamed rule"
        const val DEFAULT_PRE_CONDITION_NAME = "unnamed pre condition"
        const val DEFAULT_POST_CONDITION_NAME = "unnamed post condition"
    }
}