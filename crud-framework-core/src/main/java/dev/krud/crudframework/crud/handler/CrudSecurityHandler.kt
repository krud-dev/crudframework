package dev.krud.crudframework.crud.handler

import dev.krud.crudframework.crud.policy.Policy
import dev.krud.crudframework.crud.policy.PolicyRuleType
import dev.krud.crudframework.model.PersistentEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.FilterField

interface CrudSecurityHandler {
    fun getPolicies(clazz: Class<out PersistentEntity>): List<Policy<out PersistentEntity>>
    fun getFilterFields(clazz: Class<out PersistentEntity>): List<FilterField>
    fun evaluatePreRules(type: PolicyRuleType, clazz: Class<out PersistentEntity>): MultiPolicyResult
    fun evaluatePreRulesAndThrow(type: PolicyRuleType, clazz: Class<out PersistentEntity>) = evaluatePreRules(type, clazz).throwIfFailed()
    fun evaluatePostRules(entity: PersistentEntity, type: PolicyRuleType, clazz: Class<out PersistentEntity>): MultiPolicyResult
    fun evaluatePostRulesAndThrow(entity: PersistentEntity, type: PolicyRuleType, clazz: Class<out PersistentEntity>) = evaluatePostRules(entity, type, clazz).throwIfFailed()
    fun decorateFilter(clazz: Class<out PersistentEntity>, filter: DynamicModelFilter)
}