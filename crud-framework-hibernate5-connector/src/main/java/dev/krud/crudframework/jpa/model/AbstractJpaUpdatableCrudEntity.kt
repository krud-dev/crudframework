package dev.krud.crudframework.jpa.model

import dev.krud.shapeshift.resolver.annotation.MappedField
import org.hibernate.annotations.CreationTimestamp
import dev.krud.crudframework.jpa.ro.AbstractJpaUpdatableCrudRO
import java.util.Date
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.persistence.Version
import java.time.LocalDate

@MappedSuperclass
abstract class AbstractJpaUpdatableCrudEntity : AbstractJpaCrudEntity() {
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(nullable = false)
    @MappedField(target = AbstractJpaUpdatableCrudRO::class)
    val creationTime: LocalDate = LocalDate.now()

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @MappedField(target = AbstractJpaUpdatableCrudRO::class)
    var lastUpdateTime: LocalDate = LocalDate.now()

    @Version
    @Column(nullable = false)
    var version: Long? = null
}