package dev.krud.crudframework.mongo.model

import dev.krud.shapeshift.resolver.annotation.MappedField
import dev.krud.crudframework.mongo.ro.BaseMongoUpdatableRO
import java.util.Date

abstract class BaseMongoUpdateableEntity : BaseMongoEntity() {
    @MappedField(target = BaseMongoUpdatableRO::class)
    var lastUpdateTime: Date = Date()
}