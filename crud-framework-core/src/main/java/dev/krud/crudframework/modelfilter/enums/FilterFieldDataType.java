package dev.krud.crudframework.modelfilter.enums;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
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

    public static FilterFieldDataType get(Class clazz) {
        if (String.class.equals(clazz)) {
            return FilterFieldDataType.String;
        } else if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
            return FilterFieldDataType.Integer;
        } else if (long.class.equals(clazz) || Long.class.equals(clazz)) {
            return FilterFieldDataType.Long;
        } else if (double.class.equals(clazz) || Double.class.equals(clazz)) {
            return FilterFieldDataType.Double;
        } else if (java.util.Date.class.equals(clazz)) {
            return FilterFieldDataType.Date;
        } else if (boolean.class.equals(clazz) || Boolean.class.equals(clazz)) {
            return FilterFieldDataType.Boolean;
        } else if (Enum.class.isAssignableFrom(clazz)) {
            return FilterFieldDataType.Enum;
        } else if (UUID.class.equals(clazz)) {
            return FilterFieldDataType.UUID;
        } else if (java.math.BigInteger.class.equals(clazz)) {
            return FilterFieldDataType.BigInteger;
        } else if (java.math.BigDecimal.class.equals(clazz)) {
            return FilterFieldDataType.BigDecimal;
        }

        return FilterFieldDataType.Object;
    }
}