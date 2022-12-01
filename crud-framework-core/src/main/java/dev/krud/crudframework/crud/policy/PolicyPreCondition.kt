package dev.krud.crudframework.crud.policy

import java.security.Principal

typealias PolicyPreConditionSupplier = (Principal?) -> Boolean

data class PolicyPreCondition(
    val name: String,
    val location: PolicyElementLocation,
    val supplier: PolicyPreConditionSupplier
) {
    data class Result(
        val success: Boolean,
        val condition: PolicyPreCondition
    )
}