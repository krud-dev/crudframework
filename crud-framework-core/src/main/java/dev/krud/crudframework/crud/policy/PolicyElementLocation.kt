package dev.krud.crudframework.crud.policy

data class PolicyElementLocation(
    val fileName: String?,
    val lineNumber: Int,
    val declaringClass: String,
    val methodName: String
) {
    override fun toString(): String {
        return "$declaringClass.$methodName($fileName:$lineNumber)"
    }

    companion object {
        fun StackTraceElement.toPolicyElementLocation(): PolicyElementLocation {
            return PolicyElementLocation(
                fileName,
                lineNumber,
                className,
                methodName
            )
        }
    }
}