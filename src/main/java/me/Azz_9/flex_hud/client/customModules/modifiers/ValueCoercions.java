package me.Azz_9.flex_hud.client.customModules.modifiers;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Objects;

public final class ValueCoercions {

	private ValueCoercions() {
	}

	public static boolean canCoerce(Class<?> fromType, Class<?> toType) {
		if (toType == String.class) {
			return true;
		}

		if (isNumericType(toType)) {
			return Number.class.isAssignableFrom(fromType);
		}

		return false;
	}

	public static boolean isNumericType(Class<?> type) {
		return type == Byte.class
				|| type == Short.class
				|| type == Integer.class
				|| type == Long.class
				|| type == Float.class
				|| type == Double.class
				|| type == BigDecimal.class;
	}

	public static Object coerce(Object value, Class<?> targetType) {
		Objects.requireNonNull(targetType, "targetType");

		if (value == null) {
			return targetType == String.class ? "null" : null;
		}

		if (targetType.isInstance(value)) {
			return value;
		}

		if (targetType == String.class) {
			return String.valueOf(value);
		}

		if (isNumericType(targetType)) {
			if (targetType == BigDecimal.class) {
				return toBigDecimal(value);
			}

			double numericValue = toDouble(value);
			return castDoubleToTarget(numericValue, targetType);
		}

		throw new IllegalArgumentException("Cannot coerce " + value.getClass().getSimpleName() + " to " + targetType.getSimpleName());
	}

	public static BigDecimal toBigDecimal(Object value) {
		BigDecimal numericValue = toBigDecimalIfNumber(value);
		if (numericValue != null) {
			return numericValue;
		}

		if (value == null) {
			throw new IllegalArgumentException("Cannot coerce null to BigDecimal");
		}

		if (value instanceof String stringValue) {
			try {
				return new BigDecimal(stringValue);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Cannot parse BigDecimal from \"" + stringValue + "\"", e);
			}
		}

		throw new IllegalArgumentException("Cannot coerce " + value.getClass().getSimpleName() + " to BigDecimal");
	}

	public static @Nullable BigDecimal toBigDecimalIfNumber(@Nullable Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof BigDecimal bigDecimal) {
			return bigDecimal;
		}

		if (value instanceof Byte
				|| value instanceof Short
				|| value instanceof Integer
				|| value instanceof Long) {
			return BigDecimal.valueOf(((Number) value).longValue());
		}

		if (value instanceof Number number) {
			return BigDecimal.valueOf(number.doubleValue());
		}

		return null;
	}

	private static double toDouble(Object value) {
		if (value instanceof Number number) {
			return number.doubleValue();
		}

		if (value instanceof String stringValue) {
			try {
				return Double.parseDouble(stringValue);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Cannot parse number from \"" + stringValue + "\"", e);
			}
		}

		throw new IllegalArgumentException("Cannot coerce " + value.getClass().getSimpleName() + " to a numeric type");
	}

	private static Object castDoubleToTarget(double value, Class<?> targetType) {
		if (targetType == Double.class) {
			return value;
		}
		if (targetType == Float.class) {
			return (float) value;
		}
		if (targetType == Long.class) {
			return (long) value;
		}
		if (targetType == Integer.class) {
			return (int) value;
		}
		if (targetType == Short.class) {
			return (short) value;
		}
		if (targetType == Byte.class) {
			return (byte) value;
		}

		throw new IllegalArgumentException("Unsupported numeric target type: " + targetType.getSimpleName());
	}
}
