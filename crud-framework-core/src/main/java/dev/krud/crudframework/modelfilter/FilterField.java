package dev.krud.crudframework.modelfilter;

import dev.krud.crudframework.modelfilter.enums.FilterFieldDataType;
import dev.krud.crudframework.modelfilter.enums.FilterFieldOperation;

import jakarta.annotation.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Shani on 22/02/2018.
 */
public class FilterField implements Serializable {

    @Nullable
	private String fieldName;

	private FilterFieldOperation operation;

    @Nullable
	private FilterFieldDataType dataType;

    @Nullable
	private String enumType;

    @Nullable
	private Object[] values;

    @Nullable
	private List<FilterField> children;

	private boolean validated = false;

	public FilterField() {
	}

	public FilterField(String fieldName, FilterFieldOperation operation, FilterFieldDataType dataType, List list) {
		this.fieldName = fieldName;
		this.operation = operation;
		this.dataType = dataType;
		this.values = list.toArray();
	}

	public FilterField(String fieldName, FilterFieldOperation operation, FilterFieldDataType dataType, Object... values) {
		this.fieldName = fieldName;
		this.operation = operation;
		this.dataType = dataType;
		this.values = values;
	}

	public FilterField(String fieldName, FilterFieldOperation operation, String enumType, List list) {
		this.fieldName = fieldName;
		this.operation = operation;
		this.dataType = FilterFieldDataType.Enum;
		this.enumType = enumType;
		this.values = list.toArray();
	}

	public FilterField(String fieldName, FilterFieldOperation operation, String enumType, Object... values) {
		this.fieldName = fieldName;
		this.operation = operation;
		this.dataType = FilterFieldDataType.Enum;
		this.enumType = enumType;
		this.values = values;
	}

    @Nullable
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(@Nullable String fieldName) {
		this.fieldName = fieldName;
	}

	public FilterFieldOperation getOperation() {
		return operation;
	}

	public void setOperation(FilterFieldOperation operation) {
		this.operation = operation;
	}

    @Nullable
    public FilterFieldDataType getDataType() {
		return dataType;
	}

	public void setDataType(@Nullable FilterFieldDataType dataType) {
		this.dataType = dataType;
	}

    @Nullable
    public String getEnumType() {
		return enumType;
	}

	public void setEnumType(@Nullable String enumType) {
		this.enumType = enumType;
	}

	public boolean validated() {
		return validated;
	}

    @Nullable
    public Object[] getValues() {
		if(values != null) {
			return Arrays.stream(values).map(x -> castToType(x)).collect(Collectors.toList()).toArray();
		}

		return null;
	}

	public void setValues(@Nullable Object[] values) {
		this.values = values;
	}

    @Nullable
    public List<FilterField> getChildren() {
        return children;
    }

    public void setChildren(@Nullable List<FilterField> children) {
        this.children = children;
    }

	public Object value1() {
		if(values == null || values.length == 0) {
			return null;
		}

		return castToType(values[0]);
	}

	public Object value2() {
		if(values == null || values.length < 2) {
			return null;
		}

		return castToType(values[1]);
	}

	public void validate() {
		this.validated = true;
	}

	@Override
	public String toString() {
		return "FilterField [" +
				"fieldName=" + fieldName +
				", operation=" + operation +
				", values=" + values +
				']';
	}

	private Object castToType(Object field) {
		switch(dataType) {
			case String:
				return field.toString();
			case Integer:
				return Integer.parseInt(field.toString());
			case Long:
				return Long.parseLong(field.toString());
			case Double:
				return Double.parseDouble(field.toString());
			case Date:
				if(field instanceof Date) {
					return field;
				}
				return new Date(Long.parseLong(field.toString()));
			case Boolean:
				return Boolean.parseBoolean(field.toString());
			case Object:
				return field;
			case Enum:
				if(enumType == null || enumType.isEmpty()) {
					throw new RuntimeException("Cannot filter enum without enumType for field - " + getFieldName());
				}

				Class clazz = null;
				try {
					clazz = Class.forName(enumType);
					return Enum.valueOf(clazz, field.toString());
				} catch(ClassNotFoundException e) {
					throw new RuntimeException("Could not find class [ " + enumType + " ]");
				} catch(IllegalArgumentException e) {
					throw new RuntimeException("Could not find value [ " + field.toString() + " ] in enum [ " + enumType + " ]");
				}
            case UUID:
                return UUID.fromString(field.toString());
            case BigInteger:
                return new BigInteger(field.toString());
            case BigDecimal:
                return new BigDecimal(field.toString());
			default:
				return field;
		}
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}
		FilterField that = (FilterField) o;
		return Objects.equals(fieldName, that.fieldName) &&
				operation == that.operation &&
				dataType == that.dataType &&
				Objects.equals(enumType, that.enumType) &&
				Arrays.equals(values, that.values) &&
				Objects.equals(children, that.children);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(fieldName, operation, dataType, enumType, children);
		if(operation != null && values != null) {
			result = 31 * result + Arrays.hashCode(values);
		}

		return result;
	}
}
