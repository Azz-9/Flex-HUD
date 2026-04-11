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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Modifiers {

	private static final int IMPOSSIBLE_COST = Integer.MAX_VALUE / 4;
	private static final List<Modifier<?, ?>> MODIFIERS = new ArrayList<>();

	public static void init() {
		MODIFIERS.clear();

		// BigDecimal -> BigDecimal
		register(BigDecimal.class, BigDecimal.class, "abs", BigDecimal::abs);
		registerRegex(BigDecimal.class, BigDecimal.class, "round", Pattern.compile("round\\.(\\d{1,2})"), (val, arguments) -> val.setScale(Integer.parseInt(arguments.getFirst()), RoundingMode.HALF_UP));
		registerRegex(BigDecimal.class, BigDecimal.class, "floor", Pattern.compile("floor\\.(\\d{1,2})"), (val, arguments) -> val.setScale(Integer.parseInt(arguments.getFirst()), RoundingMode.FLOOR));
		registerRegex(BigDecimal.class, BigDecimal.class, "ceil", Pattern.compile("ceil\\.(\\d{1,2})"), (val, arguments) -> val.setScale(Integer.parseInt(arguments.getFirst()), RoundingMode.CEILING));
		register(BigDecimal.class, BigDecimal.class, "negate", BigDecimal::negate);
		registerRegex(BigDecimal.class, BigDecimal.class, "clamp", Pattern.compile("clamp\\.(-?\\d+)\\.(-?\\d+)"), (val, arguments) -> val.min(new BigDecimal(arguments.getFirst())).max(new BigDecimal(arguments.get(1))));
		registerRegex(BigDecimal.class, BigDecimal.class, "min", Pattern.compile("min\\.(-?\\d+)"), (val, arguments) -> val.min(new BigDecimal(arguments.getFirst())));
		registerRegex(BigDecimal.class, BigDecimal.class, "max", Pattern.compile("max\\.(-?\\d+)"), (val, arguments) -> val.max(new BigDecimal(arguments.getFirst())));
		registerRegex(BigDecimal.class, BigDecimal.class, "add", Pattern.compile("add\\.(-?\\d+)"), (val, arguments) -> val.add(new BigDecimal(arguments.getFirst())));
		registerRegex(BigDecimal.class, BigDecimal.class, "sub", Pattern.compile("sub\\.(-?\\d+)"), (val, arguments) -> val.subtract(new BigDecimal(arguments.getFirst())));
		registerRegex(BigDecimal.class, BigDecimal.class, "mul", Pattern.compile("mul\\.(-?\\d+)"), (val, arguments) -> val.multiply(new BigDecimal(arguments.getFirst())));
		registerRegex(BigDecimal.class, BigDecimal.class, "div", Pattern.compile("div\\.(-?[1-9][0-9]*)"), (val, arguments) -> val.divide(new BigDecimal(arguments.getFirst()), RoundingMode.HALF_UP));
		registerRegex(BigDecimal.class, BigDecimal.class, "mod", Pattern.compile("mod\\.(-?\\d+)"), (val, arguments) -> val.remainder(new BigDecimal(arguments.getFirst())));
		registerRegex(BigDecimal.class, BigDecimal.class, "pow", Pattern.compile("pow\\.(\\d+)"), (val, arguments) -> val.pow(Integer.parseInt(arguments.getFirst())));
		register(BigDecimal.class, BigDecimal.class, "sqrt", val -> val.sqrt(new MathContext(15)));
		register(BigDecimal.class, Integer.class, "sign", BigDecimal::signum);

		// BigDecimal -> String
		register(BigDecimal.class, String.class, "sign_str", val -> val.compareTo(BigDecimal.ZERO) < 0 ? val.toString() : "+" + val);
		registerRegex(BigDecimal.class, String.class, "percent", Pattern.compile("percent\\.(\\d{1,2})"), (val, arguments) -> val.multiply(BigDecimal.valueOf(100)).setScale(Integer.parseInt(arguments.getFirst()), RoundingMode.HALF_UP) + "%");

		// Integer -> String
		register(Integer.class, String.class, "roman", Modifiers::intToRoman);

		// Boolean -> String
		registerCustom(Boolean.class, String.class, "bool", Modifiers::parseBooleanArguments, (val, arguments) -> val ? arguments.getFirst() : arguments.get(1));

		// String -> String
		register(String.class, String.class, "upper", String::toUpperCase);
		register(String.class, String.class, "lower", String::toLowerCase);
		register(String.class, String.class, "title", val -> val.isEmpty() ? val : Character.toUpperCase(val.charAt(0)) + val.substring(1));
		registerCustom(String.class, String.class, "pad_left", raw -> parseWidthAndCharArguments(raw, "pad_left"), (val, arguments) -> StringUtils.leftPad(val, Integer.parseInt(arguments.getFirst()), arguments.get(1).charAt(0)));
		registerCustom(String.class, String.class, "pad_right", raw -> parseWidthAndCharArguments(raw, "pad_right"), (val, arguments) -> StringUtils.rightPad(val, Integer.parseInt(arguments.getFirst()), arguments.get(1).charAt(0)));
		registerCustom(String.class, String.class, "pad_center", raw -> parseWidthAndCharArguments(raw, "pad_center"), (val, arguments) -> StringUtils.center(val, Integer.parseInt(arguments.getFirst()), arguments.get(1).charAt(0)));
		registerRegex(String.class, String.class, "truncate", Pattern.compile("truncate\\.(\\d{1,2})"), (val, arguments) -> StringHelper.truncate(val, Integer.parseInt(arguments.getFirst()), true));
		registerCustom(String.class, String.class, "replace", Modifiers::parseReplaceArguments, (val, arguments) -> val.replace(arguments.getFirst(), arguments.get(1)));

		// conditional
		registerCustom(String.class, String.class, "if_empty", raw -> parseTextAfterPrefix(raw, "if_empty"), (val, arguments) -> val == null || val.isEmpty() ? arguments.getFirst() : val);
		registerCustom(BigDecimal.class, String.class, "if_gt", Modifiers::parseConditionalBranches, Modifiers::applyConditionalBranches);
	}

	private static <I, R> void registerCustom(Class<I> inputType,
	                                          Class<R> outputType,
	                                          String key,
	                                          Function<String, @Nullable List<String>> parser,
	                                          java.util.function.BiFunction<I, List<String>, R> modifierFunction) {
		MODIFIERS.add(new Modifier<>(key, parser, inputType, outputType, modifierFunction));
	}

	private static <I, R> void registerRegex(Class<I> inputType,
	                                         Class<R> outputType,
	                                         String key,
	                                         Pattern regex,
	                                         java.util.function.BiFunction<I, List<String>, R> modifierFunction) {
		registerCustom(inputType, outputType, key, raw -> matchRegex(regex, raw), modifierFunction);
	}

	private static <I, R> void register(Class<I> inputType, Class<R> outputType, String key, Function<I, R> modifierFunction) {
		registerCustom(inputType, outputType, key, raw -> raw.equals(key) ? List.of() : null, (val, arguments) -> modifierFunction.apply(val));
	}

	private static @Nullable List<String> matchRegex(Pattern regex, String rawInput) {
		Matcher matcher = regex.matcher(rawInput);
		if (!matcher.matches()) {
			return null;
		}

		List<String> arguments = new ArrayList<>(matcher.groupCount());
		for (int i = 1; i <= matcher.groupCount(); i++) {
			arguments.add(matcher.group(i));
		}
		return arguments;
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

	public static List<String> splitUnescaped(String input, char delimiter) {
		List<String> parts = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		boolean escaped = false;

		for (int i = 0; i < input.length(); i++) {
			char character = input.charAt(i);
			if (escaped) {
				current.append('\\').append(character);
				escaped = false;
				continue;
			}

			if (character == '\\') {
				escaped = true;
				continue;
			}

			if (character == delimiter) {
				parts.add(current.toString());
				current.setLength(0);
				continue;
			}

			current.append(character);
		}

		if (escaped) {
			current.append('\\');
		}

		parts.add(current.toString());
		return parts;
	}

	public static @Nullable ResolvedModifier<?, ?> get(String input) {
		for (Modifier<?, ?> modifier : MODIFIERS) {
			List<String> arguments = modifier.resolveArguments(input);
			if (arguments != null) {
				return new ResolvedModifier<>(modifier, arguments);
			}
		}

		return null;
	}

	public static @Nullable CompiledFormatter compileFormatter(@Nullable Class<?> inputType, List<ResolvedModifier<?, ?>> modifiers) {
		if (modifiers.isEmpty()) {
			return String::valueOf;
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
		return modifier.applyUnchecked(adaptedInput, resolvedModifier.arguments());
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

	private static @Nullable List<String> parseWidthAndCharArguments(String rawInput, String key) {
		if (!rawInput.startsWith(key + ".")) {
			return null;
		}

		int widthStart = key.length() + 1;
		int widthEnd = findNextUnescaped(rawInput, widthStart, '.');
		if (widthEnd == -1) {
			return null;
		}

		String width = rawInput.substring(widthStart, widthEnd);
		if (!width.matches("\\d{1,2}")) {
			return null;
		}

		String charToken = rawInput.substring(widthEnd + 1);
		String character = decodeSingleCharacter(charToken);
		if (character == null) {
			return null;
		}

		return List.of(width, character);
	}

	private static @Nullable List<String> parseReplaceArguments(String rawInput) {
		if (!rawInput.startsWith("replace.")) {
			return null;
		}

		SingleCharacterToken fromCharacter = parseSingleCharacterToken(rawInput, "replace.".length());
		if (fromCharacter == null || fromCharacter.nextIndex() >= rawInput.length() || rawInput.charAt(fromCharacter.nextIndex()) != '.') {
			return null;
		}

		SingleCharacterToken toCharacter = parseSingleCharacterToken(rawInput, fromCharacter.nextIndex() + 1);
		if (toCharacter == null || toCharacter.nextIndex() != rawInput.length()) {
			return null;
		}

		return List.of(fromCharacter.value(), toCharacter.value());
	}

	private static @Nullable List<String> parseBooleanArguments(String rawInput) {
		if (!rawInput.startsWith("bool.")) {
			return null;
		}

		int separator = findNextUnescaped(rawInput, "bool.".length(), '.');
		if (separator == -1) {
			return null;
		}

		String trueText = unescape(rawInput.substring("bool.".length(), separator));
		String falseText = unescape(rawInput.substring(separator + 1));
		return List.of(trueText, falseText);
	}

	private static @Nullable List<String> parseTextAfterPrefix(String rawInput, String key) {
		if (!rawInput.startsWith(key + ".")) {
			return null;
		}

		return List.of(unescape(rawInput.substring(key.length() + 1)));
	}

	private static @Nullable List<String> parseConditionalBranches(String rawInput) {
		List<String> arguments = new ArrayList<>();
		int cursor = 0;

		while (cursor < rawInput.length()) {
			ConditionalOperator operator = ConditionalOperator.match(rawInput, cursor);
			if (operator == null) {
				return null;
			}

			cursor += operator.key.length();
			if (cursor >= rawInput.length() || rawInput.charAt(cursor) != '.') {
				return null;
			}

			cursor++;
			int thresholdEnd = findNextUnescaped(rawInput, cursor, '.');
			if (thresholdEnd == -1) {
				return null;
			}

			String threshold = rawInput.substring(cursor, thresholdEnd);
			if (!threshold.matches("-?\\d+")) {
				return null;
			}

			cursor = thresholdEnd + 1;
			int nextBranchStart = findNextConditionalBranchStart(rawInput, cursor);
			String resultText = nextBranchStart == -1
					? rawInput.substring(cursor)
					: rawInput.substring(cursor, nextBranchStart - 1);

			arguments.add(operator.key);
			arguments.add(threshold);
			arguments.add(unescape(resultText));

			if (nextBranchStart == -1) {
				break;
			}

			cursor = nextBranchStart;
		}

		return arguments.isEmpty() ? null : List.copyOf(arguments);
	}

	private static String applyConditionalBranches(BigDecimal value, List<String> arguments) {
		for (int i = 0; i < arguments.size(); i += 3) {
			ConditionalOperator operator = ConditionalOperator.fromKey(arguments.get(i));
			BigDecimal threshold = new BigDecimal(arguments.get(i + 1));
			String result = arguments.get(i + 2);

			if (operator.test(value, threshold)) {
				return result;
			}
		}

		return value.toString();
	}

	private static int findNextConditionalBranchStart(String input, int textStart) {
		boolean escaped = false;
		for (int i = textStart; i < input.length(); i++) {
			char character = input.charAt(i);
			if (escaped) {
				escaped = false;
				continue;
			}

			if (character == '\\') {
				escaped = true;
				continue;
			}

			if (character != '.') {
				continue;
			}

			ConditionalOperator operator = ConditionalOperator.match(input, i + 1);
			if (operator == null) {
				continue;
			}

			int afterKey = i + 1 + operator.key.length();
			if (afterKey >= input.length() || input.charAt(afterKey) != '.') {
				continue;
			}

			int thresholdStart = afterKey + 1;
			int thresholdEnd = findNextUnescaped(input, thresholdStart, '.');
			if (thresholdEnd == -1) {
				continue;
			}

			String threshold = input.substring(thresholdStart, thresholdEnd);
			if (threshold.matches("-?\\d+")) {
				return i + 1;
			}
		}

		return -1;
	}

	private static @Nullable SingleCharacterToken parseSingleCharacterToken(String input, int start) {
		if (start >= input.length()) {
			return null;
		}

		if (input.charAt(start) == '\\') {
			if (start + 1 >= input.length()) {
				return null;
			}
			return new SingleCharacterToken(String.valueOf(input.charAt(start + 1)), start + 2);
		}

		return new SingleCharacterToken(String.valueOf(input.charAt(start)), start + 1);
	}

	private static @Nullable String decodeSingleCharacter(String rawToken) {
		SingleCharacterToken token = parseSingleCharacterToken(rawToken, 0);
		if (token == null || token.nextIndex() != rawToken.length()) {
			return null;
		}

		return token.value();
	}

	private static String unescape(String input) {
		StringBuilder unescaped = new StringBuilder(input.length());
		boolean escaped = false;

		for (int i = 0; i < input.length(); i++) {
			char character = input.charAt(i);
			if (escaped) {
				unescaped.append(character);
				escaped = false;
				continue;
			}

			if (character == '\\') {
				escaped = true;
				continue;
			}

			unescaped.append(character);
		}

		if (escaped) {
			unescaped.append('\\');
		}

		return unescaped.toString();
	}

	private static int findNextUnescaped(String input, int start, char target) {
		boolean escaped = false;
		for (int i = start; i < input.length(); i++) {
			char character = input.charAt(i);
			if (escaped) {
				escaped = false;
				continue;
			}

			if (character == '\\') {
				escaped = true;
				continue;
			}

			if (character == target) {
				return i;
			}
		}

		return -1;
	}

	private static final class BestOrder {
		private int cost = IMPOSSIBLE_COST;
		private List<ResolvedModifier<?, ?>> modifiers = List.of();
	}

	private record SingleCharacterToken(String value, int nextIndex) {
	}

	private enum ConditionalOperator {
		GREATER_THAN("if_gt") {
			@Override
			boolean test(BigDecimal value, BigDecimal threshold) {
				return value.compareTo(threshold) > 0;
			}
		},
		LOWER_THAN("if_lt") {
			@Override
			boolean test(BigDecimal value, BigDecimal threshold) {
				return value.compareTo(threshold) < 0;
			}
		},
		EQUAL("if_eq") {
			@Override
			boolean test(BigDecimal value, BigDecimal threshold) {
				return value.compareTo(threshold) == 0;
			}
		};

		private final String key;

		ConditionalOperator(String key) {
			this.key = key;
		}

		abstract boolean test(BigDecimal value, BigDecimal threshold);

		private static @Nullable ConditionalOperator match(String rawInput, int startIndex) {
			for (ConditionalOperator operator : values()) {
				if (rawInput.startsWith(operator.key, startIndex)) {
					return operator;
				}
			}

			return null;
		}

		private static ConditionalOperator fromKey(String key) {
			for (ConditionalOperator operator : values()) {
				if (operator.key.equals(key)) {
					return operator;
				}
			}

			throw new IllegalArgumentException("Unknown conditional operator " + key);
		}
	}

	@FunctionalInterface
	public interface CompiledFormatter {
		String format(@Nullable Object value);
	}

	public record ResolvedModifier<I, R>(Modifier<I, R> modifier, List<String> arguments) {
		public ResolvedModifier {
			Objects.requireNonNull(modifier, "modifier");
			Objects.requireNonNull(arguments, "arguments");
		}
	}
}
