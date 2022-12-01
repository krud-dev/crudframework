package dev.krud.crudframework.util.startup.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
internal annotation class PostStartUp(val priority: Int = 0)