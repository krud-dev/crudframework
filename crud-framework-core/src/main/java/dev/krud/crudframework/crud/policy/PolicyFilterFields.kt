package dev.krud.crudframework.crud.policy

import dev.krud.crudframework.modelfilter.FilterField
import java.security.Principal

typealias PolicyFilterFieldsSupplier = (Principal?) -> List<FilterField>
typealias PolicyFilterFieldsMatcher = (Principal?) -> Boolean

data class PolicyFilterFields(
    val name: String,
    val location: PolicyElementLocation,
    val matcher: PolicyFilterFieldsMatcher,
    val supplier: PolicyFilterFieldsSupplier
) {
    companion object {
        val DEFAULT_MATCHER: PolicyFilterFieldsMatcher = { _ -> true }
    }
}