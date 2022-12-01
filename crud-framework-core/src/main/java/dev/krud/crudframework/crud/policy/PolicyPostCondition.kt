package dev.krud.crudframework.crud.policy

import dev.krud.crudframework.model.PersistentEntity
import java.security.Principal

typealias PolicyPostConditionSupplier<RootType> = (RootType, Principal?) -> Boolean

data class PolicyPostCondition<RootType : PersistentEntity>(
    val name: String,
    val location: PolicyElementLocation,
    val supplier: PolicyPostConditionSupplier<RootType>
) {
    data class Result<RootType : PersistentEntity>(
        val success: Boolean,
        val condition: PolicyPostCondition<RootType>
    )
}