package dev.krud.crudframework.crud.handler

import dev.krud.crudframework.crud.policy.Policy
import dev.krud.crudframework.crud.policy.PolicyRuleType
import dev.krud.crudframework.crud.security.PrincipalProvider
import dev.krud.crudframework.model.PersistentEntity
import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.modelfilter.FilterField
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import java.security.Principal
import java.util.concurrent.ConcurrentHashMap

internal class CrudSecurityHandlerImpl(
    private val policies: ObjectProvider<Policy<PersistentEntity>>,
    private val principalProvider: ObjectProvider<PrincipalProvider>
) : CrudSecurityHandler, InitializingBean {
    private val policyMap = ConcurrentHashMap<Class<*>, MutableList<Policy<PersistentEntity>>>()
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    override fun afterPropertiesSet() {
        for (policy in policies.orderedStream()) {
            policyMap.computeIfAbsent(policy.clazz) { mutableListOf() }
                    .add(policy)
        }
    }

    override fun getPolicies(clazz: Class<out PersistentEntity>): List<Policy<PersistentEntity>> {
        return applicationContext.getBeansOfType(Policy::class.java)
            .values
            .filter { it.clazz == clazz }
            .toList() as List<Policy<PersistentEntity>>
    }

    override fun decorateFilter(clazz: Class<out PersistentEntity>, filter: DynamicModelFilter) {
        val principal = principalProvider.ifAvailable?.getPrincipal()
        getPolicies(clazz).forEach { policy ->
            val filterFields = policy.getFilterFields(principal)
            filter.filterFields.addAll(filterFields)
        }
    }

    override fun getFilterFields(clazz: Class<out PersistentEntity>): List<FilterField> {
        return getPolicies(clazz).flatMap { it.getFilterFields(principalProvider.getObject().getPrincipal()) }
    }

    override fun evaluatePreRules(type: PolicyRuleType, clazz: Class<out PersistentEntity>): MultiPolicyResult {
        val results = getPolicies(clazz)
            .map { it.evaluatePreRules(type, principalProvider.ifAvailable?.getPrincipal()) }
        return MultiPolicyResult(
            clazz,
            results.all { it.success },
            results
        )
    }

    override fun evaluatePostRules(entity: PersistentEntity?, type: PolicyRuleType, clazz: Class<out PersistentEntity>): MultiPolicyResult {
        if (entity == null) {
            return MultiPolicyResult(clazz, true, emptyList())
        }
        val results = getPolicies(clazz).map { it.evaluatePostRules(entity, type, principalProvider.getObject().getPrincipal()) }
        return MultiPolicyResult(clazz, results.all { it.success }, results)
    }

    private fun getPrincipal(): Principal? {
        return principalProvider.ifAvailable?.getPrincipal()
    }
}