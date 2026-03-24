package me.Azz_9.flex_hud.client.customModules.modifiers;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Modifiers {

	private static final int IMPOSSIBLE_COST = Integer.MAX_VALUE / 4;
	private static final List<Modifier<?, ?>> MODIFIERS = new ArrayList<>();

	public static void init() {
		MODIFIERS.clear();

		register(Double.class, String.class, Pattern.compile("\\d{1,2}"), (val, key) -> String.format("%." + key + "f", val));
		register(Double.class, Double.class, "abs", Math::abs);
		register(Double.class, Double.class, "round", (val) -> (double) Math.round(val));
		register(Double.class, Double.class, "floor", Math::floor);
		register(Double.class, Double.class, "ceil", Math::ceil);

		register(String.class, String.class, "upper", String::toUpperCase);
		register(String.class, String.class, "lower", String::toLowerCase);
		register(String.class, String.class, "title", (val) -> val.isEmpty() ? val : Character.toUpperCase(val.charAt(0)) + val.substring(1));
	}

	private static <I, R> void register(Class<I> inputType, Class<R> outputType, Pattern regex, BiFunction<I, String, R> modifierFunction) {
		MODIFIERS.add(new Modifier<>(regex, inputType, outputType, modifierFunction));
	}

	private static <I, R> void register(Class<I> inputType, Class<R> outputType, String key, Function<I, R> modifierFunction) {
		register(inputType, outputType, Pattern.compile(Pattern.quote(key)), (val, k) -> modifierFunction.apply(val));
	}

	public static @Nullable ResolvedModifier<?, ?> get(String input) {
		for (Modifier<?, ?> modifier : MODIFIERS) {
			if (modifier.matches(input)) {
				return new ResolvedModifier<>(modifier, input);
			}
		}

		return null;
	}

	public static <T> Function<T, String> formatterFromModifiers(List<ResolvedModifier<?, ?>> modifiers) {
		List<ResolvedModifier<?, ?>> resolvedModifiers = List.copyOf(modifiers);

		return value -> {
			Object current = value;
			if (current == null) {
				return "null";
			}

			List<ResolvedModifier<?, ?>> orderedModifiers = orderForInputType(current.getClass(), resolvedModifiers);
			for (ResolvedModifier<?, ?> resolvedModifier : orderedModifiers) {
				current = applyResolvedModifier(current, resolvedModifier);
			}

			return (String) coerce(current, String.class);
		};
	}

	private static List<ResolvedModifier<?, ?>> orderForInputType(Class<?> inputType, List<ResolvedModifier<?, ?>> modifiers) {
		if (modifiers.size() <= 1) {
			return modifiers;
		}

		BestOrder bestOrder = new BestOrder();
		searchBestOrder(inputType, new ArrayList<>(modifiers), new ArrayList<>(modifiers.size()), 0, bestOrder);

		if (bestOrder.cost == IMPOSSIBLE_COST) {
			return modifiers;
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
		return type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == Float.class || type == Double.class;
	}

	private static Object applyResolvedModifier(Object input, ResolvedModifier<?, ?> resolvedModifier) {
		Modifier<?, ?> modifier = resolvedModifier.modifier();
		Object adaptedInput = coerce(input, modifier.inputType());
		return modifier.applyUnchecked(adaptedInput, resolvedModifier.key());
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

	public record ResolvedModifier<I, R>(Modifier<I, R> modifier, String key) {
		public ResolvedModifier {
			Objects.requireNonNull(modifier, "modifier");
			Objects.requireNonNull(key, "key");
		}
	}
}
