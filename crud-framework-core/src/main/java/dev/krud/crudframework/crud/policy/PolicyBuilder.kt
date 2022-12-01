package dev.krud.crudframework.crud.policy

import dev.krud.crudframework.crud.policy.PolicyElementLocation.Companion.toPolicyElementLocation
import dev.krud.crudframework.model.PersistentEntity
import dev.krud.crudframework.modelfilter.dsl.FilterFieldsBuilder
import java.security.Principal

@PolicyDsl
class PolicyBuilder<RootType : PersistentEntity>(
    private val name: String?,
    private val location: PolicyElementLocation,
    private val clazz: Class<RootType>
) {
    private val rules = mutableListOf<PolicyRule<RootType>>()
    private val filterFields = mutableListOf<PolicyFilterFields>()

    fun filter(
        name: String? = null,
        matcher: PolicyFilterFieldsMatcher = PolicyFilterFields.DEFAULT_MATCHER,
        block: FilterFieldsBuilder<RootType>.(Principal?) -> Unit
    ) {
        val supplier: PolicyFilterFieldsSupplier = { principal ->
            val filterFieldsBuilder = FilterFieldsBuilder<RootType>()
            filterFieldsBuilder.block(principal)
            filterFieldsBuilder.build()
        }
        filterFields += PolicyFilterFields(
            name ?: DEFAULT_FILTER_NAME,
            Thread.currentThread().stackTrace[2].toPolicyElementLocation(),
            matcher,
            supplier
        )
    }

    fun canAccess(name: String? = null, block: PolicyRuleBuilder<RootType>.() -> Unit) {
        val policyRuleBuilder = PolicyRuleBuilder<RootType>(
            name,
            PolicyRuleType.CAN_ACCESS,
            Thread.currentThread().stackTrace[2].toPolicyElementLocation()
        )
        policyRuleBuilder.block()
        val action = policyRuleBuilder.build()
        rules.add(action)
    }

    fun canUpdate(name: String? = null, block: PolicyRuleBuilder<RootType>.() -> Unit) {
        val policyRuleBuilder = PolicyRuleBuilder<RootType>(
            name,
            PolicyRuleType.CAN_UPDATE,
            Thread.currentThread().stackTrace[2].toPolicyElementLocation()
        )
        policyRuleBuilder.block()
        val action = policyRuleBuilder.build()
        rules.add(action)
    }

    fun canDelete(name: String? = null, block: PolicyRuleBuilder<RootType>.() -> Unit) {
        val policyRuleBuilder = PolicyRuleBuilder<RootType>(
            name,
            PolicyRuleType.CAN_DELETE,
            Thread.currentThread().stackTrace[2].toPolicyElementLocation()
        )
        policyRuleBuilder.block()
        val action = policyRuleBuilder.build()
        rules.add(action)
    }

    fun canCreate(name: String? = null, block: PolicyRuleBuilder<RootType>.() -> Unit) {
        val policyRuleBuilder = PolicyRuleBuilder<RootType>(
            name,
            PolicyRuleType.CAN_CREATE,
            Thread.currentThread().stackTrace[2].toPolicyElementLocation()
        )
        policyRuleBuilder.block()
        val action = policyRuleBuilder.build()
        rules.add(action)
    }

    fun build(): Policy<RootType> {
        return Policy(
            name ?: DEFAULT_POLICY_NAME, location, clazz, filterFields, rules
        )
    }

    companion object {
        const val DEFAULT_POLICY_NAME = "unnamed policy"
        const val DEFAULT_FILTER_NAME = "unnamed filter"
    }
}

