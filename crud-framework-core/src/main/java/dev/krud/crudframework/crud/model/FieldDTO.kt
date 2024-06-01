package dev.krud.crudframework.crud.model

import java.lang.reflect.Field

data class FieldDTO(
    val field: Field,
    val annotations: List<Annotation>
)