package dev.krud.crudframework.modelfilter.dsl

import dev.krud.crudframework.model.PersistentEntity
import dev.krud.crudframework.modelfilter.FilterField
import dev.krud.crudframework.modelfilter.dsl.annotation.FilterFieldDsl
import dev.krud.crudframework.modelfilter.enums.FilterFieldDataType
import dev.krud.crudframework.modelfilter.enums.FilterFieldOperation
import java.util.Date
import java.util.UUID
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

@FilterFieldDsl
class FilterFieldsBuilder<RootType : PersistentEntity>(private val filterFields: MutableList<FilterField> = mutableListOf(), private val fieldPrefix: String = "") {

    infix fun KProperty1<RootType, String?>.Equal(target: String) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            target
        )
    }

    infix fun KProperty1<RootType, Int?>.Equal(target: Int) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.Equal,
            FilterFieldDataType.Integer,
            target
        )
    }

    infix fun KProperty1<RootType, Long?>.Equal(target: Long) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.Equal,
            FilterFieldDataType.Long,
            target
        )
    }

    infix fun KProperty1<RootType, Double?>.Equal(target: Double) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.Equal,
            FilterFieldDataType.Double,
            target
        )
    }

    infix fun KProperty1<RootType, Boolean?>.Equal(target: Boolean) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.Equal,
            FilterFieldDataType.Boolean,
            target
        )
    }

    infix fun KProperty1<RootType, Date?>.Equal(target: Date) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.Equal,
            FilterFieldDataType.Date,
            target
        )
    }

    infix fun <EnumType : Enum<EnumType>> KProperty1<RootType, EnumType?>.Equal(target: EnumType) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.Equal,
            target.javaClass.canonicalName,
            target
        )
    }

    infix fun KProperty1<RootType, UUID?>.Equal(target: UUID) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.Equal,
            FilterFieldDataType.UUID,
            target
        )
    }

    infix fun KProperty1<RootType, String?>.NotEqual(target: String) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.String,
            target
        )
    }

    infix fun KProperty1<RootType, Int?>.NotEqual(target: Int) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.Integer,
            target
        )
    }

    infix fun KProperty1<RootType, Long?>.NotEqual(target: Long) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.Long,
            target
        )
    }

    infix fun KProperty1<RootType, Double?>.NotEqual(target: Double) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.Double,
            target
        )
    }

    infix fun KProperty1<RootType, Boolean?>.NotEqual(target: Boolean) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.Boolean,
            target
        )
    }

    infix fun KProperty1<RootType, Date?>.NotEqual(target: Date) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.Date,
            target
        )
    }

    infix fun <EnumType : Enum<EnumType>> KProperty1<RootType, EnumType?>.NotEqual(target: EnumType) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotEqual,
            target.javaClass.canonicalName,
            target
        )
    }

    infix fun KProperty1<RootType, UUID?>.NotEqual(target: UUID) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.UUID,
            target
        )
    }

    infix fun KProperty1<RootType, String?>.Contains(target: String) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.Contains,
            FilterFieldDataType.String,
            target
        )
    }

    infix fun KProperty1<RootType, Int?>.GreaterThan(target: Int) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.GreaterThan,
            FilterFieldDataType.Integer,
            target
        )
    }

    infix fun KProperty1<RootType, Long?>.GreaterThan(target: Long) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.GreaterThan,
            FilterFieldDataType.Long,
            target
        )
    }

    infix fun KProperty1<RootType, Double?>.GreaterThan(target: Double) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.GreaterThan,
            FilterFieldDataType.Double,
            target
        )
    }

    infix fun KProperty1<RootType, Date?>.GreaterThan(target: Date) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.GreaterThan,
            FilterFieldDataType.Date,
            target
        )
    }

    infix fun KProperty1<RootType, Int?>.GreaterOrEqual(target: Int) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.GreaterEqual,
            FilterFieldDataType.Integer,
            target
        )
    }

    infix fun KProperty1<RootType, Long?>.GreaterOrEqual(target: Long) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.GreaterEqual,
            FilterFieldDataType.Long,
            target
        )
    }

    infix fun KProperty1<RootType, Double?>.GreaterOrEqual(target: Double) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.GreaterEqual,
            FilterFieldDataType.Double,
            target
        )
    }

    infix fun KProperty1<RootType, Date?>.GreaterOrEqual(target: Date) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.GreaterEqual,
            FilterFieldDataType.Date,
            target
        )
    }

    infix fun KProperty1<RootType, Int?>.LowerThan(target: Int) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.LowerThan,
            FilterFieldDataType.Integer,
            target
        )
    }

    infix fun KProperty1<RootType, Long?>.LowerThan(target: Long) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.LowerThan,
            FilterFieldDataType.Long,
            target
        )
    }

    infix fun KProperty1<RootType, Double?>.LowerThan(target: Double) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.LowerThan,
            FilterFieldDataType.Double,
            target
        )
    }

    infix fun KProperty1<RootType, Date?>.LowerThan(target: Date) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.LowerThan,
            FilterFieldDataType.Date,
            target
        )
    }

    infix fun KProperty1<RootType, Int?>.LowerOrEqual(target: Int) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.LowerEqual,
            FilterFieldDataType.Integer,
            target
        )
    }

    infix fun KProperty1<RootType, Long?>.LowerOrEqual(target: Long) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.LowerEqual,
            FilterFieldDataType.Long,
            target
        )
    }

    infix fun KProperty1<RootType, Double?>.LowerOrEqual(target: Double) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.LowerEqual,
            FilterFieldDataType.Double,
            target
        )
    }

    infix fun KProperty1<RootType, Date?>.LowerOrEqual(target: Date) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.LowerEqual,
            FilterFieldDataType.Date,
            target
        )
    }

    @JvmName("stringRequireIn")
    infix fun KProperty1<RootType, String?>.RequireIn(target: Collection<String>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.In(target)
        }
    }

    @JvmName("intRequireIn")
    infix fun KProperty1<RootType, Int?>.RequireIn(target: Collection<Int>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.In(target)
        }
    }

    @JvmName("longRequireIn")
    infix fun KProperty1<RootType, Long?>.RequireIn(target: Collection<Long>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.In(target)
        }
    }

    @JvmName("doubleRequireIn")
    infix fun KProperty1<RootType, Double?>.RequireIn(target: Collection<Double>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.In(target)
        }
    }

    @JvmName("dateRequireIn")
    infix fun KProperty1<RootType, Date?>.RequireIn(target: Collection<Date>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.In(target)
        }
    }

    @JvmName("enumRequireIn")
    infix fun <EnumType : Enum<EnumType>> KProperty1<RootType, EnumType?>.RequireIn(target: Collection<EnumType>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.In(target)
        }
    }

    @JvmName("uuidRequireIn")
    infix fun KProperty1<RootType, UUID?>.RequireIn(target: Collection<UUID>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.In(target)
        }
    }

    @JvmName("stringRequireNotIn")
    infix fun KProperty1<RootType, String?>.RequireNotIn(target: Collection<String>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.NotIn(target)
        }
    }

    @JvmName("intRequireNotIn")
    infix fun KProperty1<RootType, Int?>.RequireNotIn(target: Collection<Int>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.NotIn(target)
        }
    }

    @JvmName("longRequireNotIn")
    infix fun KProperty1<RootType, Long?>.RequireNotIn(target: Collection<Long>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.NotIn(target)
        }
    }

    @JvmName("doubleRequireNotIn")
    infix fun KProperty1<RootType, Double?>.RequireNotIn(target: Collection<Double>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.NotIn(target)
        }
    }

    @JvmName("dateRequireNotIn")
    infix fun KProperty1<RootType, Date?>.RequireNotIn(target: Collection<Date>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.NotIn(target)
        }
    }

    @JvmName("enumRequireNotIn")
    infix fun <EnumType : Enum<EnumType>> KProperty1<RootType, EnumType?>.RequireNotIn(target: Collection<EnumType>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.NotIn(target)
        }
    }

    @JvmName("uuidRequireNotIn")
    infix fun KProperty1<RootType, UUID?>.RequireNotIn(target: Collection<UUID>) {
        if (target.isEmpty()) {
            noop()
        } else {
            this.NotIn(target)
        }
    }

    @JvmName("stringIn")
    infix fun KProperty1<RootType, String?>.In(target: Collection<String>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.In,
            FilterFieldDataType.String,
            *target.toTypedArray()
        )
    }

    @JvmName("intIn")
    infix fun KProperty1<RootType, Int?>.In(target: Collection<Int>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.In,
            FilterFieldDataType.Integer,
            *target.toTypedArray()
        )
    }

    @JvmName("longIn")
    infix fun KProperty1<RootType, Long?>.In(target: Collection<Long>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.In,
            FilterFieldDataType.Long,
            *target.toTypedArray()
        )
    }

    @JvmName("doubleIn")
    infix fun KProperty1<RootType, Double?>.In(target: Collection<Double>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.In,
            FilterFieldDataType.Double,
            *target.toTypedArray()
        )
    }

    @JvmName("dateIn")
    infix fun KProperty1<RootType, Date?>.In(target: Collection<Date>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.In,
            FilterFieldDataType.Date,
            *target.toTypedArray()
        )
    }

    @JvmName("enumIn")
    infix fun <EnumType : Enum<EnumType>> KProperty1<RootType, EnumType?>.In(target: Collection<EnumType>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.In,
            target.first()::class.java.canonicalName,
            *(target as Collection<*>).toTypedArray()
        )
    }

    @JvmName("uuidIn")
    infix fun KProperty1<RootType, UUID?>.In(target: Collection<UUID>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.In,
            FilterFieldDataType.UUID,
            *target.toTypedArray()
        )
    }

    @JvmName("stringNotIn")
    infix fun KProperty1<RootType, String?>.NotIn(target: Collection<String>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotIn,
            FilterFieldDataType.String,
            *target.toTypedArray()
        )
    }

    @JvmName("intNotIn")
    infix fun KProperty1<RootType, Int?>.NotIn(target: Collection<Int>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotIn,
            FilterFieldDataType.Integer,
            *target.toTypedArray()
        )
    }

    @JvmName("longNotIn")
    infix fun KProperty1<RootType, Long?>.NotIn(target: Collection<Long>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotIn,
            FilterFieldDataType.Long,
            *target.toTypedArray()
        )
    }

    @JvmName("doubleNotIn")
    infix fun KProperty1<RootType, Double?>.NotIn(target: Collection<Double>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotIn,
            FilterFieldDataType.Double,
            *target.toTypedArray()
        )
    }

    @JvmName("dateNotIn")
    infix fun KProperty1<RootType, Date?>.NotIn(target: Collection<Date>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotIn,
            FilterFieldDataType.Date,
            *target.toTypedArray()
        )
    }

    @JvmName("enumNotIn")
    infix fun <EnumType : Enum<EnumType>> KProperty1<RootType, EnumType?>.NotIn(target: Collection<EnumType>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotIn,
            target.first()::class.java.canonicalName,
            *(target as Collection<*>).toTypedArray()
        )
    }

    @JvmName("uuidNotIn")
    infix fun KProperty1<RootType, UUID?>.NotIn(target: Collection<UUID>) {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.NotIn,
            FilterFieldDataType.UUID,
            *target.toTypedArray()
        )
    }

    infix fun KProperty1<RootType, Int?>.Between(target: Int): BetweenBuilder<Int> {
        return BetweenBuilder(effectiveName, target, FilterFieldDataType.Integer)
    }

    infix fun KProperty1<RootType, Long?>.Between(target: Long): BetweenBuilder<Long> {
        return BetweenBuilder(effectiveName, target, FilterFieldDataType.Long)
    }

    infix fun KProperty1<RootType, Double?>.Between(target: Double): BetweenBuilder<Double> {
        return BetweenBuilder(effectiveName, target, FilterFieldDataType.Double)
    }

    infix fun KProperty1<RootType, Date?>.Between(target: Date): BetweenBuilder<Date> {
        return BetweenBuilder(effectiveName, target, FilterFieldDataType.Date)
    }

    infix fun KProperty1<RootType, *>.isNull(condition: Boolean) {
        when (condition) {
            true -> filterFields += FilterField(
                effectiveName,
                FilterFieldOperation.IsNull,
                FilterFieldDataType.Object,
                null,
                null
            )

            false -> filterFields += FilterField(
                effectiveName,
                FilterFieldOperation.IsNotNull,
                FilterFieldDataType.Object,
                null,
                null
            )
        }
    }

    fun KProperty1<RootType, *>.isNull() {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.IsNull,
            FilterFieldDataType.Object,
            null,
            null
        )
    }

    fun KProperty1<RootType, *>.isNotNull() {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.IsNotNull,
            FilterFieldDataType.Object,
            null,
            null
        )
    }

    fun KProperty1<RootType, Collection<*>>.isEmpty() {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.IsEmpty,
            FilterFieldDataType.Object,
            null,
            null
        )
    }

    fun KProperty1<RootType, Collection<*>>.isNotEmpty() {
        filterFields += FilterField(
            effectiveName,
            FilterFieldOperation.IsNotEmpty,
            FilterFieldDataType.Object,
            null,
            null
        )
    }

    infix fun <ChildType : PersistentEntity> KProperty1<RootType, ChildType?>.Sub(setup: FilterFieldsBuilder<ChildType>.() -> Unit) {
        val prefix = if (fieldPrefix.isEmpty()) name else "$fieldPrefix/$name"
        val filterFieldsBuilder = FilterFieldsBuilder<ChildType>(fieldPrefix = prefix)
        setup(filterFieldsBuilder)

        filterFields += filterFieldsBuilder.build()
    }

    infix fun <ChildType : PersistentEntity> KProperty1<RootType, Collection<ChildType>?>.SubCollection(setup: FilterFieldsBuilder<ChildType>.() -> Unit) {
        val prefix = if (fieldPrefix.isEmpty()) name else "$fieldPrefix/$name"
        val filterFieldsBuilder = FilterFieldsBuilder<ChildType>(fieldPrefix = prefix)
        setup(filterFieldsBuilder)

        filterFields += filterFieldsBuilder.build()
    }

    fun and(setup: FilterFieldsBuilder<RootType>.() -> Unit) {
        val filterFieldsBuilder = FilterFieldsBuilder<RootType>()
        setup(filterFieldsBuilder)

        filterFields += FilterField().apply {
            this.children = filterFieldsBuilder.build()
            this.operation = FilterFieldOperation.And
        }
    }

    fun or(setup: FilterFieldsBuilder<RootType>.() -> Unit) {
        val filterFieldsBuilder = FilterFieldsBuilder<RootType>()
        setup(filterFieldsBuilder)

        filterFields += FilterField().apply {
            this.children = filterFieldsBuilder.build()
            this.operation = FilterFieldOperation.Or
        }
    }

    fun not(setup: FilterFieldsBuilder<RootType>.() -> Unit) {
        val filterFieldsBuilder = FilterFieldsBuilder<RootType>()
        setup(filterFieldsBuilder)

        filterFields += FilterField().apply {
            this.children = filterFieldsBuilder.build()
            this.operation = FilterFieldOperation.Not
        }
    }

    fun noop() {
        filterFields += FilterField().apply {
            this.operation = FilterFieldOperation.Noop
        }
    }

    fun add(filterField: FilterField) {
        filterFields += filterField
    }

    infix fun <T> BetweenBuilder<T>.And(target: T) {
        filterFields += this.build(target)
    }

    fun build() = filterFields.toList()

    private val KProperty<*>.effectiveName: String
        get() {
            return if (fieldPrefix.isEmpty()) name else "$fieldPrefix.$name"
        }
}