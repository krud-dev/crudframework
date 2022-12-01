package dev.krud.crudframework.web.ro

data class ManyFailedReason<FailedObject>(
    val `object`: FailedObject,
    val reason: String
)