package dev.krud.crudframework.mongo.model

import dev.krud.shapeshift.resolver.annotation.MappedField
import org.springframework.data.annotation.Id
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.mongo.annotation.MongoCrudEntity
import dev.krud.crudframework.mongo.ro.BaseMongoRO
import java.util.Date

@MongoCrudEntity
abstract class BaseMongoEntity : BaseCrudEntity<String>() {
    @MappedField(target = BaseMongoRO::class)
    @Id
    override lateinit var id: String

    @MappedField(target = BaseMongoRO::class)
    var creationTime: Date = Date()

    override fun exists(): Boolean = this::id.isInitialized
}