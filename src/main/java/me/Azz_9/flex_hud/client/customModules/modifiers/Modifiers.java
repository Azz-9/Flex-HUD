package me.Azz_9.flex_hud.client.customModules.modifiers;

import net.minecraft.util.StringHelper;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Modifiers {

	private static final int IMPOSSIBLE_COST = Integer.MAX_VALUE / 4;
	private static final List<Modifier<?, ?>> MODIFIERS = new ArrayList<>();

	public static void init() {
		MODIFIERS.clear();

		// BigDecomal -> BigDecimal
		register(BigDecimal.class, BigDecimal.class, "abs", BigDecimal::abs);
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("round\\.(\\d{1,2})"), (val, matcher) -> val.setScale(Integer.parseInt(matcher.group(1)), RoundingMode.HALF_UP));
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("floor\\.(\\d{1,2})"), (val, matcher) -> val.setScale(Integer.parseInt(matcher.group(1)), RoundingMode.FLOOR));
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("ceil\\.(\\d{1,2})"), (val, matcher) -> val.setScale(Integer.parseInt(matcher.group(1)), RoundingMode.CEILING));
		register(BigDecimal.class, BigDecimal.class, "negate", BigDecimal::negate);
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("clamp\\.(\\d+)\\.(\\d+)"), (val, matcher) -> val.min(new BigDecimal(matcher.group(1))).max(new BigDecimal(matcher.group(2))));
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("min\\.(\\d+)"), (val, matcher) -> val.min(new BigDecimal(matcher.group(1))));
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("max\\.(\\d+)"), (val, matcher) -> val.max(new BigDecimal(matcher.group(1))));
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("add\\.(\\d+)"), (val, matcher) -> val.add(new BigDecimal(matcher.group(1))));
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("sub\\.(\\d+)"), (val, matcher) -> val.subtract(new BigDecimal(matcher.group(1))));
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("mul\\.(\\d+)"), (val, matcher) -> val.multiply(new BigDecimal(matcher.group(1))));
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("div\\.([1-9][0-9]*)"), (val, matcher) -> val.divide(new BigDecimal(matcher.group(1)), RoundingMode.HALF_UP));
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("mod\\.(\\d+)"), (val, matcher) -> val.remainder(new BigDecimal(matcher.group(1))));
		register(BigDecimal.class, BigDecimal.class, Pattern.compile("pow\\.(\\d+)"), (val, matcher) -> val.pow(Integer.parseInt(matcher.group(1))));
		register(BigDecimal.class, BigDecimal.class, "sqrt", (val) -> val.sqrt(new MathContext(15)));
		register(BigDecimal.class, Integer.class, "sign", BigDecimal::signum);

		// BigDecomal -> String
		register(BigDecimal.class, String.class, "sign_str", (val) -> val.compareTo(BigDecimal.valueOf(0)) < 0 ? val.toString() : "+" + val);
		register(BigDecimal.class, String.class, Pattern.compile("percent\\.(\\d{1,2})"), (val, matcher) -> val.multiply(BigDecimal.valueOf(100)).setScale(Integer.parseInt(matcher.group(1)), RoundingMode.HALF_UP) + "%");

		// Integer -> String
		register(Integer.class, String.class, "roman", Modifiers::intToRoman);

		// Boolean -> String
		register(Boolean.class, String.class, Pattern.compile("bool\\.(.+)\\.(.+)"), (val, matcher) -> val ? matcher.group(1) : matcher.group(2));

		// String -> String
		register(String.class, String.class, "upper", String::toUpperCase);
		register(String.class, String.class, "lower", String::toLowerCase);
		register(String.class, String.class, "title", (val) -> val.isEmpty() ? val : Character.toUpperCase(val.charAt(0)) + val.substring(1));
		register(String.class, String.class, Pattern.compile("pad_left\\.(\\d{1,2})\\.(.)"), (val, matcher) -> StringUtils.leftPad(val, Integer.parseInt(matcher.group(1)), matcher.group(2)));
		register(String.class, String.class, Pattern.compile("pad_right\\.(\\d{1,2})\\.(.)"), (val, matcher) -> StringUtils.rightPad(val, Integer.parseInt(matcher.group(1)), matcher.group(2)));
		register(String.class, String.class, Pattern.compile("pad_center\\.(\\d{1,2})\\.(.)"), (val, matcher) -> StringUtils.center(val, Integer.parseInt(matcher.group(1)), matcher.group(2)));
		register(String.class, String.class, Pattern.compile("truncate\\.(\\d{1,2})"), (val, matcher) -> StringHelper.truncate(val, Integer.parseInt(matcher.group(1)), true));
		register(String.class, String.class, Pattern.compile("replace\\.(.)\\.(.)"), (val, matcher) -> val.replace(matcher.group(1), matcher.group(2)));

		// conditional
		register(String.class, String.class, Pattern.compile("if_empty\\.(.+)"), (val, matcher) -> val == null || val.isEmpty() ? matcher.group(1) : val);
		register(BigDecimal.class, String.class, Pattern.compile("if_gt\\.(\\d+)\\.(.+)"), (val, matcher) -> val.compareTo(new BigDecimal(matcher.group(1))) > 0 ? matcher.group(2) : val.toString());
		register(BigDecimal.class, String.class, Pattern.compile("if_lt\\.(\\d+)\\.(.+)"), (val, matcher) -> val.compareTo(new BigDecimal(matcher.group(1))) < 0 ? matcher.group(2) : val.toString());
		register(BigDecimal.class, String.class, Pattern.compile("if_eq\\.(\\d+)\\.(.+)"), (val, matcher) -> val.compareTo(new BigDecimal(matcher.group(1))) == 0 ? matcher.group(2) : val.toString());
	}

	private static <I, R> void register(Class<I> inputType, Class<R> outputType, Pattern regex, BiFunction<I, Matcher, R> modifierFunction) {
		MODIFIERS.add(new Modifier<>(regex, inputType, outputType, modifierFunction));
	}

	private static <I, R> void register(Class<I> inputType, Class<R> outputType, String key, Function<I, R> modifierFunction) {
		register(inputType, outputType, Pattern.compile(Pattern.quote(key)), (val, k) -> modifierFunction.apply(val));
	}

	private static String intToRoman(int num) {
		int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
		String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			while (num >= values[i]) {
				num -= values[i];
				sb.append(symbols[i]);
			}
		}
		return sb.toString();
	}

	public static @Nullable ResolvedModifier<?, ?> get(String input) {
		for (Modifier<?, ?> modifier : MODIFIERS) {
			Matcher matcher = modifier.getRegex().matcher(input);
			if (matcher.matches()) {
				return new ResolvedModifier<>(modifier, matcher);
			}
		}

		return null;
	}

	public static @Nullable CompiledFormatter compileFormatter(@Nullable Class<?> inputType, List<ResolvedModifier<?, ?>> modifiers) {
		if (modifiers.isEmpty()) {
			return value -> String.valueOf(value);
		}

		if (inputType == null) {
			return null;
		}

		List<ResolvedModifier<?, ?>> orderedModifiers = orderForInputType(inputType, List.copyOf(modifiers));
		if (orderedModifiers == null) {
			return null;
		}

		return value -> {
			Object current = value;
			if (current == null) {
				return "null";
			}

			for (ResolvedModifier<?, ?> resolvedModifier : orderedModifiers) {
				current = applyResolvedModifier(current, resolvedModifier);
			}

			return (String) coerce(current, String.class);
		};
	}

	public static <T> Function<T, String> formatterFromModifiers(List<ResolvedModifier<?, ?>> modifiers) {
		List<ResolvedModifier<?, ?>> resolvedModifiers = List.copyOf(modifiers);

		return value -> {
			CompiledFormatter formatter = compileFormatter(value != null ? value.getClass() : null, resolvedModifiers);
			if (formatter == null) {
				return String.valueOf(value);
			}

			return formatter.format(value);
		};
	}

	private static @Nullable List<ResolvedModifier<?, ?>> orderForInputType(Class<?> inputType, List<ResolvedModifier<?, ?>> modifiers) {
		if (modifiers.size() <= 1) {
			return modifiers;
		}

		BestOrder bestOrder = new BestOrder();
		searchBestOrder(inputType, new ArrayList<>(modifiers), new ArrayList<>(modifiers.size()), 0, bestOrder);

		if (bestOrder.cost == IMPOSSIBLE_COST) {
			return null;
		}

		return bestOrder.modifiers;
	}

	private static void searchBestOrder(Class<?> currentType,
	                                    List<ResolvedModifier<?, ?>> remaining,
	                                    List<ResolvedModifier<?, ?>> path,
	                                    int currentCost,
	                                    BestOrder bestOrder) {
		if (currentCost >= bestOrder.cost) {
			return;
		}

		if (remaining.isEmpty()) {
			int totalCost = currentCost + transitionCost(currentType, String.class);
			if (totalCost < bestOrder.cost) {
				bestOrder.cost = totalCost;
				bestOrder.modifiers = List.copyOf(path);
			}
			return;
		}

		for (int i = 0; i < remaining.size(); i++) {
			ResolvedModifier<?, ?> candidate = remaining.remove(i);
			Class<?> candidateInputType = candidate.modifier().inputType();
			int transitionCost = transitionCost(currentType, candidateInputType);

			if (transitionCost < IMPOSSIBLE_COST) {
				path.add(candidate);
				searchBestOrder(candidate.modifier().outputType(), remaining, path, currentCost + transitionCost, bestOrder);
				path.removeLast();
			}

			remaining.add(i, candidate);
		}
	}

	private static int transitionCost(Class<?> fromType, Class<?> toType) {
		if (toType.isAssignableFrom(fromType)) {
			return 0;
		}

		if (canCoerce(fromType, toType)) {
			return 1;
		}

		return IMPOSSIBLE_COST;
	}

	private static boolean canCoerce(Class<?> fromType, Class<?> toType) {
		if (toType == String.class) {
			return true;
		}

		if (isNumericType(toType)) {
			return fromType == String.class || Number.class.isAssignableFrom(fromType);
		}

		return false;
	}

	private static boolean isNumericType(Class<?> type) {
		return type == Byte.class
				|| type == Short.class
				|| type == Integer.class
				|| type == Long.class
				|| type == Float.class
				|| type == Double.class
				|| type == BigDecimal.class;
	}

	private static Object applyResolvedModifier(Object input, ResolvedModifier<?, ?> resolvedModifier) {
		Modifier<?, ?> modifier = resolvedModifier.modifier();
		Object adaptedInput = coerce(input, modifier.inputType());
		return modifier.applyUnchecked(adaptedInput, resolvedModifier.matcher);
	}

	private static Object coerce(Object value, Class<?> targetType) {
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

	private static BigDecimal toBigDecimal(Object value) {
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

		if (value instanceof String stringValue) {
			try {
				return new BigDecimal(stringValue);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Cannot parse BigDecimal from \"" + stringValue + "\"", e);
			}
		}

		throw new IllegalArgumentException("Cannot coerce " + value.getClass().getSimpleName() + " to BigDecimal");
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

	private static final class BestOrder {
		private int cost = IMPOSSIBLE_COST;
		private List<ResolvedModifier<?, ?>> modifiers = List.of();
	}

	@FunctionalInterface
	public interface CompiledFormatter {
		String format(@Nullable Object value);
	}

	public record ResolvedModifier<I, R>(Modifier<I, R> modifier, Matcher matcher) {
		public ResolvedModifier {
			Objects.requireNonNull(modifier, "modifier");
			Objects.requireNonNull(matcher, "matcher");
		}
	}
}
