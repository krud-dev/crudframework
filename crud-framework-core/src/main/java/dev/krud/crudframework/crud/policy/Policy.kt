package dev.krud.crudframework.crud.policy

import com.github.tomaslanger.chalk.Chalk
import dev.krud.crudframework.crud.policy.PolicyElementLocation.Companion.toPolicyElementLocation
import dev.krud.crudframework.crud.policy.PolicyRule.Companion.evaluatePostConditions
import dev.krud.crudframework.crud.policy.PolicyRule.Companion.evaluatePreConditions
import dev.krud.crudframework.model.PersistentEntity
import dev.krud.crudframework.modelfilter.FilterField
import java.security.Principal

class Policy<RootType : PersistentEntity>(
    val name: String,
    val location: PolicyElementLocation,
    val clazz: Class<RootType>,
    private val filterFields: List<PolicyFilterFields>,
    private val rules: List<PolicyRule<RootType>>
) {
    private val canAccessRules: List<PolicyRule<RootType>> by lazy {
        rules.filter { it.type == PolicyRuleType.CAN_ACCESS }
    }
    private val canUpdateRules: List<PolicyRule<RootType>> by lazy {
        rules.filter { it.type == PolicyRuleType.CAN_UPDATE }
    }
    private val canDeleteRules: List<PolicyRule<RootType>> by lazy {
        rules.filter { it.type == PolicyRuleType.CAN_DELETE }
    }
    private val canCreateRules: List<PolicyRule<RootType>> by lazy {
        rules.filter { it.type == PolicyRuleType.CAN_CREATE }
    }

    fun getFilterFields(principal: Principal?): List<FilterField> {
        return filterFields.flatMap { it.supplier(principal) }
    }

    fun evaluatePreRules(type: PolicyRuleType, principal: Principal?): Result<RootType> {
        val ruleResults = when (type) {
            PolicyRuleType.CAN_ACCESS -> canAccessRules.evaluatePreConditions(principal)
            PolicyRuleType.CAN_CREATE -> canAccessRules.evaluatePreConditions(principal) + canCreateRules.evaluatePreConditions(principal)
            PolicyRuleType.CAN_UPDATE -> canAccessRules.evaluatePreConditions(principal) + canUpdateRules.evaluatePreConditions(principal)
            PolicyRuleType.CAN_DELETE -> canAccessRules.evaluatePreConditions(principal) + canDeleteRules.evaluatePreConditions(principal)
        }
        return Result(
            ruleResults.all { it.success },
            this,
            ruleResults
        )
    }

    fun evaluatePostRules(entity: RootType, type: PolicyRuleType, principal: Principal?): Result<RootType> {
        val ruleResults = when (type) {
            PolicyRuleType.CAN_ACCESS -> canAccessRules.evaluatePostConditions(entity, principal)
            PolicyRuleType.CAN_CREATE -> error("Post rules cannot be evaluated for create")
            PolicyRuleType.CAN_UPDATE -> canAccessRules.evaluatePostConditions(entity, principal) + canUpdateRules.evaluatePostConditions(entity, principal)
            PolicyRuleType.CAN_DELETE -> canAccessRules.evaluatePostConditions(entity, principal) + canDeleteRules.evaluatePostConditions(entity, principal)
        }
        return Result(
            ruleResults.all { it.success },
            this,
            ruleResults
        )
    }

    fun evaluatePreCanAccess(principal: Principal?): Result<RootType> {
        val result = canAccessRules.map { it.evaluatePreConditions(principal) }
        return Result(
            result.all { it.success },
            this,
            result
        )
    }

    data class Result<RootType : PersistentEntity>(
        val success: Boolean,
        val policy: Policy<RootType>,
        val ruleResults: List<PolicyRule.Result<RootType>>
    ) {
        operator fun plus(other: Result<RootType>): Result<RootType> {
            return Result(
                success && other.success,
                policy,
                ruleResults + other.ruleResults
            )
        }

        fun outputShortOutcome(): String {
            val sb = StringBuilder()
            sb.appendLine("${Chalk.on(policy.name).bold()} - ${appendSuccess(success)}")
            ruleResults.forEachIndexed { ruleIndex, ruleResult ->
                sb.appendLine("\t${ruleIndex + 1}. ${Chalk.on(ruleResult.rule.name).bold()} - ${appendSuccess(ruleResult.success)}")
                ruleResult.preConditionResults.forEachIndexed { conditionIndex, conditionResult ->
                    sb.appendLine("\t\t${conditionIndex + 1}. ${Chalk.on(conditionResult.condition.name).bold()} - ${appendSuccess(conditionResult.success)}")
                }
                ruleResult.postConditionResults.forEachIndexed { conditionIndex, conditionResult ->
                    sb.appendLine("\t\t${conditionIndex + 1}. ${Chalk.on(conditionResult.condition.name).bold()} - ${appendSuccess(conditionResult.success)}")
                }
            }
            return sb.toString()
        }

        private fun appendSuccess(success: Boolean): String {
            return if (success) {
                Chalk.on("PASSED").bold().green().toString()
            } else {
                Chalk.on("FAILED").bold().red().toString()
            }
        }

        companion object {
            @JvmName("policyListDistinctRuleTypes")
            fun Collection<Policy.Result<out PersistentEntity>>.distinctRuleTypes(): Set<PolicyRuleType> {
                return this.flatMap { it.ruleResults.distinctRuleTypes() }.toSet()
            }

            @JvmName("policyRuleListDistinctRuleTypes")
            fun Collection<PolicyRule.Result<*>>.distinctRuleTypes(): Set<PolicyRuleType> {
                return this.map { it.rule.type }.toSet()
            }
        }
    }
}

inline fun <reified RootType : PersistentEntity> policy(
    name: String? = null,
    block: PolicyBuilder<RootType>.() -> Unit
): Policy<RootType> {
    val policyBuilder = PolicyBuilder(name, Thread.currentThread().stackTrace[1].toPolicyElementLocation(), RootType::class.java)
    policyBuilder.block()
    return policyBuilder.build()
}