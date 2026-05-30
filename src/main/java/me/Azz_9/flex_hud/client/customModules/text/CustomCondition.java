package me.Azz_9.flex_hud.client.customModules.text;

import static java.util.Objects.requireNonNull;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;

public final class CustomCondition {

	public static final String PREFIX = "if:";
	private static final Pattern DECIMAL_LITERAL = Pattern.compile("-?\\d+(\\.\\d+)?");

	private CustomCondition() {
	}

	public static @Nullable Condition parse(String source, Function<String, @Nullable Variable<?>> variableResolver) {
		List<RawTerm> rawTerms = splitTopLevelTerms(source);
		if (rawTerms.isEmpty()) {
			return null;
		}

		List<Term> terms = new ArrayList<>(rawTerms.size());
		for (RawTerm rawTerm : rawTerms) {
			Clause clause = parseClause(rawTerm.rawClause(), variableResolver);
			if (clause == null) {
				return null;
			}
			terms.add(new Term(rawTerm.connector(), clause));
		}

		return new Condition(terms);
	}

	public static Condition defaultCondition(Variable<?> variable) {
		return new Condition(List.of(new Term(Connector.AND, new Clause(new Operand(variable.getKey(), variable, List.of()), Operator.GREATER_THAN, BigDecimal.ZERO))));
	}

	private static @Nullable Clause parseClause(String rawClause, Function<String, @Nullable Variable<?>> variableResolver) {
		String clause = rawClause.trim();
		if (clause.isEmpty()) {
			return null;
		}

		OperatorMatch operatorMatch = findOperator(clause);
		if (operatorMatch == null) {
			return null;
		}

		Operand operand = parseOperand(clause.substring(0, operatorMatch.index()), variableResolver);
		if (operand == null) {
			return null;
		}

		String thresholdText = clause.substring(operatorMatch.index() + operatorMatch.symbol().length()).trim();
		if (!DECIMAL_LITERAL.matcher(thresholdText).matches()) {
			return null;
		}

		return new Clause(operand, operatorMatch.operator(), new BigDecimal(thresholdText));
	}

	private static @Nullable Operand parseOperand(String rawOperand, Function<String, @Nullable Variable<?>> variableResolver) {
		String operand = unwrapPlaceholder(rawOperand.trim());
		if (operand.isEmpty()) {
			return null;
		}

		List<String> parts = Modifiers.splitUnescaped(operand, ':');
		String key = parts.getFirst().trim();
		Variable<?> variable = variableResolver.apply(key);
		if (variable == null) {
			return null;
		}

		List<Modifiers.ResolvedModifier<?, ?>> modifiers = new ArrayList<>(Math.max(0, parts.size() - 1));
		for (int i = 1; i < parts.size(); i++) {
			Modifiers.ResolvedModifier<?, ?> modifier = Modifiers.get(parts.get(i));
			if (modifier == null) {
				return null;
			}
			modifiers.add(modifier);
		}

		return new Operand(key, variable, modifiers);
	}

	private static String unwrapPlaceholder(String operand) {
		if (operand.length() < 2 || operand.charAt(0) != '{' || operand.charAt(operand.length() - 1) != '}') {
			return operand;
		}

		int end = findMatchingDelimiter(operand, 1, '{', '}');
		if (end == operand.length() - 1) {
			return operand.substring(1, operand.length() - 1);
		}

		return operand;
	}

	private static @Nullable OperatorMatch findOperator(String clause) {
		int curlyDepth = 0;
		int bracketDepth = 0;
		boolean escaped = false;

		for (int index = 0; index < clause.length(); index++) {
			char current = clause.charAt(index);
			if (escaped) {
				escaped = false;
				continue;
			}

			if (current == '\\') {
				escaped = true;
				continue;
			}

			if (current == '{') {
				curlyDepth++;
				continue;
			}
			if (current == '}') {
				curlyDepth = Math.max(0, curlyDepth - 1);
				continue;
			}
			if (current == '[') {
				bracketDepth++;
				continue;
			}
			if (current == ']') {
				bracketDepth = Math.max(0, bracketDepth - 1);
				continue;
			}

			if (curlyDepth == 0 && bracketDepth == 0) {
				for (Operator operator : Operator.values()) {
					for (String symbol : operator.symbols()) {
						if (clause.startsWith(symbol, index)) {
							return new OperatorMatch(operator, symbol, index);
						}
					}
				}
			}
		}

		return null;
	}

	private static List<RawTerm> splitTopLevelTerms(String source) {
		List<RawTerm> terms = new ArrayList<>();
		int curlyDepth = 0;
		int bracketDepth = 0;
		boolean escaped = false;
		int partStart = 0;
		Connector nextConnector = Connector.AND;

		for (int index = 0; index < source.length(); index++) {
			char current = source.charAt(index);
			if (escaped) {
				escaped = false;
				continue;
			}

			if (current == '\\') {
				escaped = true;
				continue;
			}

			if (current == '{') {
				curlyDepth++;
				continue;
			}
			if (current == '}') {
				curlyDepth = Math.max(0, curlyDepth - 1);
				continue;
			}
			if (current == '[') {
				bracketDepth++;
				continue;
			}
			if (current == ']') {
				bracketDepth = Math.max(0, bracketDepth - 1);
				continue;
			}

			if (curlyDepth == 0 && bracketDepth == 0) {
				ConnectorMatch connectorMatch = findConnector(source, index);
				if (connectorMatch == null) {
					continue;
				}

				String rawClause = source.substring(partStart, index);
				if (rawClause.isBlank()) {
					return List.of();
				}
				terms.add(new RawTerm(nextConnector, rawClause));
				nextConnector = connectorMatch.connector();
				index += connectorMatch.length() - 1;
				partStart = index + 1;
			}
		}

		String rawClause = source.substring(partStart);
		if (rawClause.isBlank()) {
			return List.of();
		}
		terms.add(new RawTerm(nextConnector, rawClause));
		return List.copyOf(terms);
	}

	private static @Nullable ConnectorMatch findConnector(String source, int index) {
		if (source.startsWith(Connector.AND.symbol(), index)) {
			return new ConnectorMatch(Connector.AND, Connector.AND.symbol().length());
		}
		if (source.startsWith(Connector.OR.symbol(), index)) {
			return new ConnectorMatch(Connector.OR, Connector.OR.symbol().length());
		}

		for (Connector connector : Connector.values()) {
			for (String keyword : connector.keywords()) {
				if (matchesWordConnector(source, index, keyword)) {
					return new ConnectorMatch(connector, keyword.length());
				}
			}
		}
		return null;
	}

	private static boolean matchesWordConnector(String source, int index, String keyword) {
		if (index + keyword.length() > source.length() || !source.regionMatches(true, index, keyword, 0, keyword.length())) {
			return false;
		}

		return isWordConnectorBoundary(source, index - 1) && isWordConnectorBoundary(source, index + keyword.length());
	}

	private static boolean isWordConnectorBoundary(String source, int index) {
		return index < 0 || index >= source.length() || Character.isWhitespace(source.charAt(index));
	}

	private static int findMatchingDelimiter(String source, int start, char open, char close) {
		int depth = 0;
		boolean escaped = false;

		for (int cursor = start; cursor < source.length(); cursor++) {
			char current = source.charAt(cursor);
			if (escaped) {
				escaped = false;
				continue;
			}

			if (current == '\\') {
				escaped = true;
				continue;
			}

			if (current == open) {
				depth++;
			} else if (current == close) {
				if (depth == 0) {
					return cursor;
				}
				depth--;
			}
		}

		return -1;
	}

	public record Condition(List<Term> terms) {
		public Condition {
			terms = List.copyOf(terms);
		}

		public List<Clause> clauses() {
			return terms.stream().map(Term::clause).toList();
		}

		public boolean test() {
			if (terms.isEmpty()) {
				return false;
			}

			boolean result = false;
			boolean currentAndGroup = terms.getFirst().clause().test();
			for (int index = 1; index < terms.size(); index++) {
				Term term = terms.get(index);
				if (term.connector() == Connector.OR) {
					result |= currentAndGroup;
					currentAndGroup = term.clause().test();
				} else {
					currentAndGroup &= term.clause().test();
				}
			}
			return result || currentAndGroup;
		}

		public List<Variable<?>> dependencies() {
			List<Variable<?>> dependencies = new ArrayList<>();
			for (Term term : terms) {
				Variable<?> variable = term.clause().operand().variable();
				if (!dependencies.contains(variable)) {
					dependencies.add(variable);
				}
			}
			return List.copyOf(dependencies);
		}

		public String format() {
			if (terms.isEmpty()) {
				return "";
			}

			StringBuilder builder = new StringBuilder(terms.getFirst().clause().format());
			for (int index = 1; index < terms.size(); index++) {
				Term term = terms.get(index);
				builder.append(term.connector().symbol()).append(term.clause().format());
			}
			return builder.toString();
		}

		public String displayText() {
			return displayText(Connector::displayName);
		}

		public String displayText(Function<Connector, String> connectorFormatter) {
			if (terms.isEmpty()) {
				return "";
			}

			StringBuilder builder = new StringBuilder(terms.getFirst().clause().displayText());
			for (int index = 1; index < terms.size(); index++) {
				Term term = terms.get(index);
				builder.append(' ')
						.append(connectorFormatter.apply(term.connector()))
						.append(' ')
						.append(term.clause().displayText());
			}
			return builder.toString();
		}
	}

	public record Term(Connector connector, Clause clause) {
		public Term {
			requireNonNull(connector, "connector");
			requireNonNull(clause, "clause");
		}
	}

	public record Clause(Operand operand, Operator operator, BigDecimal threshold) {
		public Clause {
			requireNonNull(operand, "operand");
			requireNonNull(operator, "operator");
			requireNonNull(threshold, "threshold");
		}

		public boolean test() {
			Object value = Modifiers.applyValueModifiers(operand.variable().getValue(), operand.modifiers());
			BigDecimal numericValue = toBigDecimal(value);
			return numericValue != null && operator.test(numericValue, threshold);
		}

		public String format() {
			return operand.format() + operator.primarySymbol() + threshold.toPlainString();
		}

		public String displayText() {
			return operand.displayText() + " " + operator.primarySymbol() + " " + threshold.toPlainString();
		}
	}

	public record Operand(String key, Variable<?> variable, List<Modifiers.ResolvedModifier<?, ?>> modifiers) {
		public Operand {
			requireNonNull(key, "key");
			requireNonNull(variable, "variable");
			modifiers = List.copyOf(modifiers);
		}

		public String format() {
			StringBuilder builder = new StringBuilder(key);
			for (Modifiers.ResolvedModifier<?, ?> modifier : modifiers) {
				builder.append(':').append(Modifiers.formatRaw(modifier));
			}
			return builder.toString();
		}

		public String displayText() {
			if (modifiers.isEmpty()) {
				return key;
			}
			return format();
		}
	}

	public enum Operator {
		GREATER_OR_EQUAL(">=", List.of(">=")) {
			@Override
			boolean test(BigDecimal value, BigDecimal threshold) {
				return value.compareTo(threshold) >= 0;
			}
		},
		LOWER_OR_EQUAL("<=", List.of("<=")) {
			@Override
			boolean test(BigDecimal value, BigDecimal threshold) {
				return value.compareTo(threshold) <= 0;
			}
		},
		NOT_EQUAL("!=", List.of("!=")) {
			@Override
			boolean test(BigDecimal value, BigDecimal threshold) {
				return value.compareTo(threshold) != 0;
			}
		},
		EQUAL("=", List.of("==", "=")) {
			@Override
			boolean test(BigDecimal value, BigDecimal threshold) {
				return value.compareTo(threshold) == 0;
			}
		},
		GREATER_THAN(">", List.of(">")) {
			@Override
			boolean test(BigDecimal value, BigDecimal threshold) {
				return value.compareTo(threshold) > 0;
			}
		},
		LOWER_THAN("<", List.of("<")) {
			@Override
			boolean test(BigDecimal value, BigDecimal threshold) {
				return value.compareTo(threshold) < 0;
			}
		};

		private final String primarySymbol;
		private final List<String> symbols;

		Operator(String primarySymbol, List<String> symbols) {
			this.primarySymbol = primarySymbol;
			this.symbols = List.copyOf(symbols);
		}

		public String primarySymbol() {
			return primarySymbol;
		}

		public List<String> symbols() {
			return symbols;
		}

		abstract boolean test(BigDecimal value, BigDecimal threshold);
	}

	public enum Connector {
		AND("and", "&&", "AND", List.of("and", "et")),
		OR("or", "||", "OR", List.of("or", "ou"));

		private final String key;
		private final String symbol;
		private final String displayName;
		private final List<String> keywords;

		Connector(String key, String symbol, String displayName, List<String> keywords) {
			this.key = key;
			this.symbol = symbol;
			this.displayName = displayName;
			this.keywords = List.copyOf(keywords);
		}

		public String key() {
			return key;
		}

		public String symbol() {
			return symbol;
		}

		public String displayName() {
			return displayName;
		}

		public List<String> keywords() {
			return keywords;
		}

		public Connector next() {
			return this == AND ? OR : AND;
		}
	}

	private record RawTerm(Connector connector, String rawClause) {
		private RawTerm {
			requireNonNull(connector, "connector");
			requireNonNull(rawClause, "rawClause");
		}
	}

	private record ConnectorMatch(Connector connector, int length) {
		private ConnectorMatch {
			requireNonNull(connector, "connector");
		}
	}

	private record OperatorMatch(Operator operator, String symbol, int index) {
		private OperatorMatch {
			requireNonNull(operator, "operator");
			requireNonNull(symbol, "symbol");
		}
	}

	private static @Nullable BigDecimal toBigDecimal(@Nullable Object value) {
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

		if (value instanceof String stringValue) {
			try {
				return new BigDecimal(stringValue);
			} catch (NumberFormatException ignored) {
				return null;
			}
		}

		return null;
	}
}
