package dev.krud.crudframework.modelfilter.enums

enum class FilterFieldOperation(val junction: Boolean = false) {
    Equal,
    EqualIgnoreCase,
    NotEqual,
    NotEqualIgnoreCase,
    In,
    NotIn,
    GreaterThan,
    GreaterEqual,
    LowerThan,
    LowerEqual,
    Between,
    Contains,
    IsNull,
    IsNotNull,
    IsEmpty,
    IsNotEmpty,
    And(true),
    Or(true),
    Not(true),
    Noop;
}