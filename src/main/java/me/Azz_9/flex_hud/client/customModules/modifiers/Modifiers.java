package me.Azz_9.flex_hud.client.customModules.modifiers;

import net.minecraft.util.StringUtil;

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

import me.Azz_9.flex_hud.client.customModules.CustomModuleSyntax;

public class Modifiers {

	private static final List<Modifier<?, ?>> MODIFIERS = new ArrayList<>();

	public static void init() {
		MODIFIERS.clear();
		registerBigDecimalModifiers();
		registerBigDecimalStringModifiers();
		registerIntegerStringModifiers();
		registerBooleanStringModifiers();
		registerStringModifiers();
		registerConditionalModifiers();
	}

	private static void registerBigDecimalModifiers() {
		register(BigDecimal.class, BigDecimal.class, "abs", BigDecimal::abs);
		registerRegex(
				BigDecimal.class, BigDecimal.class,
				"round", Pattern.compile("round\\.(\\d{1,2})"),
				(val, arguments) -> val.setScale(Integer.parseInt(arguments.getFirst()), RoundingMode.HALF_UP),
				uiFixed("round",
						List.of(parameter("digits", Modifier.ParameterKind.INTEGER)),
						arguments -> formatFixedRaw("round", List.of(parameter("digits", Modifier.ParameterKind.INTEGER)), arguments))
		);
		registerRegex(
				BigDecimal.class, BigDecimal.class,
				"floor", Pattern.compile("floor\\.(\\d{1,2})"),
				(val, arguments) -> val.setScale(Integer.parseInt(arguments.getFirst()), RoundingMode.FLOOR),
				uiFixed("floor",
						List.of(parameter("digits", Modifier.ParameterKind.INTEGER)),
						arguments -> formatFixedRaw("floor", List.of(parameter("digits", Modifier.ParameterKind.INTEGER)), arguments))
		);
		registerRegex(
				BigDecimal.class, BigDecimal.class,
				"ceil", Pattern.compile("ceil\\.(\\d{1,2})"),
				(val, arguments) -> val.setScale(Integer.parseInt(arguments.getFirst()), RoundingMode.CEILING),
				uiFixed("ceil",
						List.of(parameter("digits", Modifier.ParameterKind.INTEGER)),
						arguments -> formatFixedRaw("ceil", List.of(parameter("digits", Modifier.ParameterKind.INTEGER)), arguments))
		);
		register(BigDecimal.class, BigDecimal.class, "negate", BigDecimal::negate);
		registerRegex(
				BigDecimal.class, BigDecimal.class,
				"clamp", Pattern.compile("clamp\\.(-?\\d+)\\.(-?\\d+)"),
				(val, arguments) -> val.min(new BigDecimal(arguments.getFirst())).max(new BigDecimal(arguments.get(1))),
				uiFixed("clamp",
						List.of(
								parameter("min", Modifier.ParameterKind.DECIMAL),
								parameter("max", Modifier.ParameterKind.DECIMAL)
						),
						arguments -> formatFixedRaw("clamp", List.of(
								parameter("min", Modifier.ParameterKind.DECIMAL),
								parameter("max", Modifier.ParameterKind.DECIMAL)
						), arguments))
		);
		registerRegex(BigDecimal.class, BigDecimal.class, "min", Pattern.compile("min\\.(-?\\d+)"), (val, arguments) -> val.min(new BigDecimal(arguments.getFirst())), uiFixed("min", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments -> formatFixedRaw("min", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments)));
		registerRegex(BigDecimal.class, BigDecimal.class, "max", Pattern.compile("max\\.(-?\\d+)"), (val, arguments) -> val.max(new BigDecimal(arguments.getFirst())), uiFixed("max", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments -> formatFixedRaw("max", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments)));
		registerRegex(BigDecimal.class, BigDecimal.class, "add", Pattern.compile("add\\.(-?\\d+)"), (val, arguments) -> val.add(new BigDecimal(arguments.getFirst())), uiFixed("add", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments -> formatFixedRaw("add", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments)));
		registerRegex(BigDecimal.class, BigDecimal.class, "sub", Pattern.compile("sub\\.(-?\\d+)"), (val, arguments) -> val.subtract(new BigDecimal(arguments.getFirst())), uiFixed("sub", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments -> formatFixedRaw("sub", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments)));
		registerRegex(BigDecimal.class, BigDecimal.class, "mul", Pattern.compile("mul\\.(-?\\d+)"), (val, arguments) -> val.multiply(new BigDecimal(arguments.getFirst())), uiFixed("mul", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments -> formatFixedRaw("mul", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments)));
		registerRegex(BigDecimal.class, BigDecimal.class, "div", Pattern.compile("div\\.(-?[1-9][0-9]*)"), (val, arguments) -> val.divide(new BigDecimal(arguments.getFirst()), RoundingMode.HALF_UP), uiFixed("div", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments -> formatFixedRaw("div", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments)));
		registerRegex(BigDecimal.class, BigDecimal.class, "mod", Pattern.compile("mod\\.(-?\\d+)"), (val, arguments) -> val.remainder(new BigDecimal(arguments.getFirst())), uiFixed("mod", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments -> formatFixedRaw("mod", List.of(parameter("value", Modifier.ParameterKind.DECIMAL)), arguments)));
		registerRegex(BigDecimal.class, BigDecimal.class, "pow", Pattern.compile("pow\\.(\\d+)"), (val, arguments) -> val.pow(Integer.parseInt(arguments.getFirst())), uiFixed("pow", List.of(parameter("value", Modifier.ParameterKind.INTEGER)), arguments -> formatFixedRaw("pow", List.of(parameter("value", Modifier.ParameterKind.INTEGER)), arguments)));
		register(BigDecimal.class, BigDecimal.class, "sqrt", val -> val.sqrt(new MathContext(15)));
		register(BigDecimal.class, Integer.class, "sign", BigDecimal::signum);
	}

	private static void registerBigDecimalStringModifiers() {
		register(BigDecimal.class, String.class, "sign_str", val -> val.compareTo(BigDecimal.ZERO) < 0 ? val.toString() : "+" + val);
		registerRegex(BigDecimal.class, String.class, "percent", Pattern.compile("percent\\.(\\d{1,2})"), (val, arguments) -> val.multiply(BigDecimal.valueOf(100)).setScale(Integer.parseInt(arguments.getFirst()), RoundingMode.HALF_UP) + "%", uiFixed("percent", List.of(parameter("digits", Modifier.ParameterKind.INTEGER)), arguments -> formatFixedRaw("percent", List.of(parameter("digits", Modifier.ParameterKind.INTEGER)), arguments)));
	}

	private static void registerIntegerStringModifiers() {
		register(Integer.class, String.class, "roman", Modifiers::intToRoman);
	}

	private static void registerBooleanStringModifiers() {
		registerCustom(Boolean.class, String.class, "bool", Modifiers::parseBooleanArguments, (val, arguments) -> val ? arguments.getFirst() : arguments.get(1), uiFixed("bool", List.of(parameter("true_value", Modifier.ParameterKind.TEXT), parameter("false_value", Modifier.ParameterKind.TEXT)), arguments -> formatFixedRaw("bool", List.of(parameter("true_value", Modifier.ParameterKind.TEXT), parameter("false_value", Modifier.ParameterKind.TEXT)), arguments)));
	}

	private static void registerStringModifiers() {
		register(String.class, String.class, "upper", String::toUpperCase);
		register(String.class, String.class, "lower", String::toLowerCase);
		register(String.class, String.class, "title", val -> val.isEmpty() ? val : Character.toUpperCase(val.charAt(0)) + val.substring(1));
		registerCustom(String.class, String.class, "pad_left", raw -> parseWidthAndCharArguments(raw, "pad_left"), (val, arguments) -> StringUtils.leftPad(val, Integer.parseInt(arguments.getFirst()), arguments.get(1).charAt(0)), uiFixed("pad_left", List.of(parameter("width", Modifier.ParameterKind.INTEGER), parameter("character", Modifier.ParameterKind.CHARACTER)), arguments -> formatFixedRaw("pad_left", List.of(parameter("width", Modifier.ParameterKind.INTEGER), parameter("character", Modifier.ParameterKind.CHARACTER)), arguments)));
		registerCustom(String.class, String.class, "pad_right", raw -> parseWidthAndCharArguments(raw, "pad_right"), (val, arguments) -> StringUtils.rightPad(val, Integer.parseInt(arguments.getFirst()), arguments.get(1).charAt(0)), uiFixed("pad_right", List.of(parameter("width", Modifier.ParameterKind.INTEGER), parameter("character", Modifier.ParameterKind.CHARACTER)), arguments -> formatFixedRaw("pad_right", List.of(parameter("width", Modifier.ParameterKind.INTEGER), parameter("character", Modifier.ParameterKind.CHARACTER)), arguments)));
		registerCustom(String.class, String.class, "pad_center", raw -> parseWidthAndCharArguments(raw, "pad_center"), (val, arguments) -> StringUtils.center(val, Integer.parseInt(arguments.getFirst()), arguments.get(1).charAt(0)), uiFixed("pad_center", List.of(parameter("width", Modifier.ParameterKind.INTEGER), parameter("character", Modifier.ParameterKind.CHARACTER)), arguments -> formatFixedRaw("pad_center", List.of(parameter("width", Modifier.ParameterKind.INTEGER), parameter("character", Modifier.ParameterKind.CHARACTER)), arguments)));
		registerRegex(String.class, String.class, "truncate", Pattern.compile("truncate\\.(\\d{1,2})"), (val, arguments) -> StringUtil.truncateStringIfNecessary(val, Integer.parseInt(arguments.getFirst()), true), uiFixed("truncate", List.of(parameter("length", Modifier.ParameterKind.INTEGER)), arguments -> formatFixedRaw("truncate", List.of(parameter("length", Modifier.ParameterKind.INTEGER)), arguments)));
		registerCustom(String.class, String.class, "replace", Modifiers::parseReplaceArguments, (val, arguments) -> val.replace(arguments.getFirst(), arguments.get(1)), uiFixed("replace", List.of(parameter("from", Modifier.ParameterKind.CHARACTER), parameter("to", Modifier.ParameterKind.CHARACTER)), arguments -> formatFixedRaw("replace", List.of(parameter("from", Modifier.ParameterKind.CHARACTER), parameter("to", Modifier.ParameterKind.CHARACTER)), arguments)));
	}

	private static void registerConditionalModifiers() {
		registerCustom(String.class, String.class, "if_empty", raw -> parseTextAfterPrefix(raw, "if_empty"), (val, arguments) -> val == null || val.isEmpty() ? arguments.getFirst() : val, uiFixed("if_empty", List.of(parameter("value", Modifier.ParameterKind.TEXT)), arguments -> formatFixedRaw("if_empty", List.of(parameter("value", Modifier.ParameterKind.TEXT)), arguments)));
		registerCustom(BigDecimal.class, String.class, "conditional", Modifiers::parseConditionalBranches, Modifiers::applyConditionalBranches, uiConditional());
	}

	private static <I, R> void registerCustom(Class<I> inputType,
	                                          Class<R> outputType,
	                                          String key,
	                                          Function<String, @Nullable List<String>> parser,
	                                          java.util.function.BiFunction<I, List<String>, R> modifierFunction) {
		registerCustom(inputType, outputType, key, parser, modifierFunction, uiNone(key));
	}

	private static <I, R> void registerCustom(Class<I> inputType,
	                                          Class<R> outputType,
	                                          String key,
	                                          Function<String, @Nullable List<String>> parser,
	                                          java.util.function.BiFunction<I, List<String>, R> modifierFunction,
	                                          Modifier.UiMetadata uiMetadata) {
		MODIFIERS.add(new Modifier<>(key, parser, inputType, outputType, modifierFunction, uiMetadata));
	}

	private static <I, R> void registerRegex(Class<I> inputType,
	                                         Class<R> outputType,
	                                         String key,
	                                         Pattern regex,
	                                         java.util.function.BiFunction<I, List<String>, R> modifierFunction) {
		registerRegex(inputType, outputType, key, regex, modifierFunction, uiNone(key));
	}

	private static <I, R> void registerRegex(Class<I> inputType,
	                                         Class<R> outputType,
	                                         String key,
	                                         Pattern regex,
	                                         java.util.function.BiFunction<I, List<String>, R> modifierFunction,
	                                         Modifier.UiMetadata uiMetadata) {
		registerCustom(inputType, outputType, key, raw -> matchRegex(regex, raw), modifierFunction, uiMetadata);
	}

	private static <I, R> void register(Class<I> inputType, Class<R> outputType, String key, Function<I, R> modifierFunction) {
		registerCustom(inputType, outputType, key, raw -> raw.equals(key) ? List.of() : null, (val, arguments) -> modifierFunction.apply(val), uiNone(key));
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

	public static @Nullable ResolvedModifier<?, ?> get(String input) {
		for (Modifier<?, ?> modifier : MODIFIERS) {
			List<String> arguments = modifier.resolveArguments(input);
			if (arguments != null) {
				return new ResolvedModifier<>(modifier, arguments, input);
			}
		}

		return null;
	}

	public static List<Modifier<?, ?>> getAll() {
		return List.copyOf(MODIFIERS);
	}

	public static @Nullable CompiledFormatter compileFormatter(@Nullable Class<?> inputType, List<ResolvedModifier<?, ?>> modifiers) {
		return ModifierChains.compileFormatter(inputType, modifiers);
	}

	public static @Nullable Object applyValueModifiers(@Nullable Object value, List<ResolvedModifier<?, ?>> modifiers) {
		return ModifierChains.applyValueModifiers(value, modifiers);
	}

	public static @Nullable Class<?> resolveOutputType(@Nullable Class<?> inputType, List<ResolvedModifier<?, ?>> modifiers) {
		return ModifierChains.resolveOutputType(inputType, modifiers);
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

	private static @Nullable List<String> parseWidthAndCharArguments(String rawInput, String key) {
		if (!rawInput.startsWith(key + ".")) {
			return null;
		}

		int widthStart = key.length() + 1;
		int widthEnd = CustomModuleSyntax.findNextUnescaped(rawInput, widthStart, '.');
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

		int separator = CustomModuleSyntax.findNextUnescaped(rawInput, "bool.".length(), '.');
		if (separator == -1) {
			return null;
		}

		String trueText = CustomModuleSyntax.unescape(rawInput.substring("bool.".length(), separator));
		String falseText = CustomModuleSyntax.unescape(rawInput.substring(separator + 1));
		return List.of(trueText, falseText);
	}

	private static @Nullable List<String> parseTextAfterPrefix(String rawInput, String key) {
		if (!rawInput.startsWith(key + ".")) {
			return null;
		}

		return List.of(CustomModuleSyntax.unescape(rawInput.substring(key.length() + 1)));
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
			int thresholdEnd = CustomModuleSyntax.findNextUnescaped(rawInput, cursor, '.');
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
			arguments.add(CustomModuleSyntax.unescape(resultText));

			if (nextBranchStart == -1) {
				break;
			}

			cursor = nextBranchStart;
		}

		return arguments.isEmpty() ? null : List.copyOf(arguments);
	}

	public static String formatRaw(ResolvedModifier<?, ?> resolvedModifier) {
		return resolvedModifier.modifier().uiMetadata().rawFormatter().apply(resolvedModifier.arguments());
	}

	private static Modifier.UiMetadata uiNone(String key) {
		return new Modifier.UiMetadata(
				Modifier.EditorKind.NONE,
				List.of(),
				arguments -> key,
				arguments -> ""
		);
	}

	private static Modifier.UiMetadata uiFixed(String key,
	                                           List<Modifier.ParameterDefinition> parameters,
	                                           Function<List<String>, String> rawFormatter) {
		return new Modifier.UiMetadata(
				parameters.isEmpty() ? Modifier.EditorKind.NONE : Modifier.EditorKind.FIXED_FIELDS,
				parameters,
				rawFormatter,
				Modifiers::formatDisplayArguments
		);
	}

	private static Modifier.UiMetadata uiConditional() {
		return new Modifier.UiMetadata(
				Modifier.EditorKind.CONDITIONAL_BRANCHES,
				List.of(parameter("branches", Modifier.ParameterKind.CONDITIONAL_BRANCHES)),
				Modifiers::formatConditionalBranches,
				Modifiers::formatConditionalDisplay
		);
	}

	private static Modifier.ParameterDefinition parameter(String key, Modifier.ParameterKind kind) {
		return new Modifier.ParameterDefinition(key, kind);
	}

	private static String formatFixedRaw(String key,
	                                     List<Modifier.ParameterDefinition> parameters,
	                                     List<String> arguments) {
		StringBuilder builder = new StringBuilder(key);
		for (int i = 0; i < Math.min(parameters.size(), arguments.size()); i++) {
			builder.append('.').append(escapeParameter(arguments.get(i), parameters.get(i).kind()));
		}
		return builder.toString();
	}

	private static String escapeParameter(String value, Modifier.ParameterKind kind) {
		return switch (kind) {
			case CHARACTER -> escapeCharacter(value);
			case INTEGER, DECIMAL, TEXT, CONDITIONAL_BRANCHES -> escapeText(value);
		};
	}

	private static String escapeText(String value) {
		return CustomModuleSyntax.escape(value, ".");
	}

	private static String escapeCharacter(String value) {
		if (value == null || value.isEmpty()) {
			return " ";
		}

		String character = value.substring(0, 1);
		return escapeText(character);
	}

	private static String formatDisplayArguments(List<String> arguments) {
		if (arguments.isEmpty()) {
			return "";
		}

		return String.join(", ", arguments.stream()
				.map(argument -> argument.equals(" ") ? "' '" : argument)
				.toList());
	}

	private static String formatConditionalBranches(List<String> arguments) {
		if (arguments.isEmpty()) {
			return "if_gt.0.";
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i + 2 < arguments.size(); i += 3) {
			if (!builder.isEmpty()) {
				builder.append('.');
			}

			builder.append(arguments.get(i))
					.append('.')
					.append(arguments.get(i + 1))
					.append('.')
					.append(escapeText(arguments.get(i + 2)));
		}
		return builder.isEmpty() ? "if_gt.0." : builder.toString();
	}

	private static String formatConditionalDisplay(List<String> arguments) {
		List<String> branches = new ArrayList<>();
		for (int i = 0; i + 2 < arguments.size(); i += 3) {
			String operator = switch (arguments.get(i)) {
				case "if_gt" -> ">";
				case "if_lt" -> "<";
				case "if_eq" -> "=";
				default -> arguments.get(i);
			};
			branches.add(operator + " " + arguments.get(i + 1) + " -> " + arguments.get(i + 2));
		}
		return branches.isEmpty() ? "" : String.join(" | ", branches);
	}

	private static String applyConditionalBranches(BigDecimal value, List<String> arguments) {
		for (int i = 0; i + 2 < arguments.size(); i += 3) {
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
			int thresholdEnd = CustomModuleSyntax.findNextUnescaped(input, thresholdStart, '.');
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

	public record ResolvedModifier<I, R>(Modifier<I, R> modifier, List<String> arguments, String rawInput) {
		public ResolvedModifier {
			Objects.requireNonNull(modifier, "modifier");
			Objects.requireNonNull(arguments, "arguments");
			Objects.requireNonNull(rawInput, "rawInput");
		}
	}
}
