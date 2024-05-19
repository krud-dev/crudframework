package dev.krud.crudframework.modelfilter.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum FilterFieldDataType {
    String(java.lang.String.class),
    Integer(java.lang.Integer.class),
    Long(java.lang.Long.class),
    Double(java.lang.Double.class),
    Boolean(java.lang.Boolean.class),
    Date(java.util.Date.class),
    Object(java.lang.Object.class),
    Enum(java.lang.Enum.class),
    UUID(java.util.UUID.class),
    BigInteger(java.math.BigInteger.class),
    BigDecimal(java.math.BigDecimal.class),
    None;

    private static final Map<Class, FilterFieldDataType> lookup = new HashMap<>();

    static {
        for (FilterFieldDataType dataType : EnumSet.allOf(FilterFieldDataType.class)) {
            lookup.put(dataType.getMatchingClass(), dataType);
        }
    }

    private Class matchingClass = null;

    FilterFieldDataType(Class matchingClass) {
        this.matchingClass = matchingClass;
    }

    FilterFieldDataType() {
    }

    public Class getMatchingClass() {
        return matchingClass;
    }

    public static FilterFieldDataType get(Class matchingClass) {
        // todo: add unboxed -> boxed type conversion
        return lookup.get(matchingClass);
    }
}