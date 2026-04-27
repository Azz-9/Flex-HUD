package me.Azz_9.flex_hud.client.customModules.text;

import static java.util.Objects.requireNonNull;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;

public final class CustomTextParser {

	private static final Map<String, NamedDirective> NAMED_DIRECTIVES = new HashMap<>();
	private static final List<NamedDirective> NAMED_DIRECTIVES_BY_LENGTH = new ArrayList<>();
	private static final Map<Character, Directive> SINGLE_CHAR_DIRECTIVES = new HashMap<>();
	private static final Map<String, Integer> NAMED_COLORS = new HashMap<>();

	static {
		registerNamedColor("black", 0x000000, '0');
		registerNamedColor("dark_blue", 0x0000aa, '1');
		registerNamedColor("dark_green", 0x00aa00, '2');
		registerNamedColor("dark_aqua", 0x00aaaa, '3');
		registerNamedColor("dark_red", 0xaa0000, '4');
		registerNamedColor("dark_purple", 0xaa00aa, '5');
		registerNamedColor("gold", 0xffaa00, '6');
		registerNamedColor("gray", 0xaaaaaa, '7');
		registerNamedColor("dark_gray", 0x555555, '8');
		registerNamedColor("blue", 0x5555ff, '9');
		registerNamedColor("green", 0x55ff55, 'a');
		registerNamedColor("aqua", 0x55ffff, 'b');
		registerNamedColor("red", 0xff5555, 'c');
		registerNamedColor("light_purple", 0xff55ff, 'd');
		registerNamedColor("yellow", 0xffff55, 'e');
		registerNamedColor("white", 0xffffff, 'f');

		registerNamedColorAlias("darkblue", "dark_blue");
		registerNamedColorAlias("darkgreen", "dark_green");
		registerNamedColorAlias("darkaqua", "dark_aqua");
		registerNamedColorAlias("darkred", "dark_red");
		registerNamedColorAlias("darkpurple", "dark_purple");
		registerNamedColorAlias("grey", "gray");
		registerNamedColorAlias("darkgrey", "dark_gray");
		registerNamedColorAlias("lightpurple", "light_purple");

		registerNamedDirective("bold", ToggleDirective.BOLD);
		registerNamedDirective("italic", ToggleDirective.ITALIC);
		registerNamedDirective("underline", ToggleDirective.UNDERLINE);
		registerNamedDirective("underlined", ToggleDirective.UNDERLINE);
		registerNamedDirective("strikethrough", ToggleDirective.STRIKETHROUGH);
		registerNamedDirective("strike", ToggleDirective.STRIKETHROUGH);
		registerNamedDirective("obfuscated", ToggleDirective.OBFUSCATED);
		registerNamedDirective("magic", ToggleDirective.OBFUSCATED);
		registerNamedDirective("reset", ResetDirective.INSTANCE);
		registerNamedDirective("chroma", new ColorDirective(ColorDirectiveKind.CHROMA, null));

		registerSingleCharDirective('k', ToggleDirective.OBFUSCATED);
		registerSingleCharDirective('l', ToggleDirective.BOLD);
		registerSingleCharDirective('m', ToggleDirective.STRIKETHROUGH);
		registerSingleCharDirective('n', ToggleDirective.UNDERLINE);
		registerSingleCharDirective('o', ToggleDirective.ITALIC);
		registerSingleCharDirective('r', ResetDirective.INSTANCE);

		NAMED_DIRECTIVES_BY_LENGTH.addAll(NAMED_DIRECTIVES.values());
		NAMED_DIRECTIVES_BY_LENGTH.sort(Comparator.comparingInt((NamedDirective directive) -> directive.key().length()).reversed());
	}

	private CustomTextParser() {
	}

	public static ParsedDocument parse(String source, Function<String, @Nullable Variable<?>> variableResolver) {
		return new Parser(source, variableResolver).parse();
	}

	public record ParsedDocument(SequenceNode root, boolean hasExplicitColors, boolean hasDynamicColors) {
		public ParsedDocument {
			requireNonNull(root, "root");
		}
	}

	public sealed interface Node permits SequenceNode, LiteralNode, VariableNode, DirectiveNode, GradientNode {
	}

	public record SequenceNode(List<Node> children) implements Node {
		public SequenceNode {
			children = List.copyOf(children);
		}
	}

	public record LiteralNode(String text) implements Node {
		public LiteralNode {
			requireNonNull(text, "text");
		}
	}

	public record VariableNode(String rawPlaceholder,
	                           String key,
	                           Variable<?> variable,
	                           List<Modifiers.ResolvedModifier<?, ?>> modifiers) implements Node {
		public VariableNode {
			requireNonNull(rawPlaceholder, "rawPlaceholder");
			requireNonNull(key, "key");
			requireNonNull(variable, "variable");
			modifiers = List.copyOf(modifiers);
		}
	}

	public record DirectiveNode(Directive directive) implements Node {
		public DirectiveNode {
			requireNonNull(directive, "directive");
		}
	}

	public record GradientNode(int startColor, int endColor, SequenceNode content) implements Node {
		public GradientNode {
			requireNonNull(content, "content");
		}
	}

	public sealed interface Directive permits ToggleDirective, ResetDirective, ColorDirective {
	}

	public enum ToggleDirective implements Directive {
		BOLD,
		ITALIC,
		UNDERLINE,
		STRIKETHROUGH,
		OBFUSCATED
	}

	public enum ResetDirective implements Directive {
		INSTANCE
	}

	public record ColorDirective(ColorDirectiveKind kind, @Nullable Integer rgb) implements Directive {
		public ColorDirective {
			requireNonNull(kind, "kind");
			if (kind == ColorDirectiveKind.STATIC) {
				requireNonNull(rgb, "rgb");
			}
		}
	}

	public enum ColorDirectiveKind {
		STATIC,
		CHROMA
	}

	private record NamedDirective(String key, Directive directive) {
	}

	private static final class Parser {
		private final String source;
		private final Function<String, @Nullable Variable<?>> variableResolver;

		private int index;
		private boolean hasExplicitColors;
		private boolean hasDynamicColors;

		private Parser(String source, Function<String, @Nullable Variable<?>> variableResolver) {
			this.source = requireNonNull(source, "source");
			this.variableResolver = requireNonNull(variableResolver, "variableResolver");
		}

		private ParsedDocument parse() {
			return new ParsedDocument(parseSequence(false), hasExplicitColors, hasDynamicColors);
		}

		private SequenceNode parseSequence(boolean stopAtRightBracket) {
			List<Node> nodes = new ArrayList<>();
			StringBuilder literal = new StringBuilder();

			while (index < source.length()) {
				char current = source.charAt(index);

				if (stopAtRightBracket && current == ']') {
					break;
				}

				if (current == '\\') {
					if (index + 1 < source.length()) {
						literal.append(source.charAt(index + 1));
						index += 2;
					} else {
						literal.append(current);
						index++;
					}
					continue;
				}

				if (current == '{') {
					flushLiteral(literal, nodes);
					nodes.add(parseVariable());
					continue;
				}

				if (current == '&') {
					flushLiteral(literal, nodes);
					nodes.add(parseDirective());
					continue;
				}

				if (current == '[') {
					flushLiteral(literal, nodes);
					nodes.add(parseGradient());
					continue;
				}

				literal.append(current);
				index++;
			}

			flushLiteral(literal, nodes);
			return new SequenceNode(nodes);
		}

		private void flushLiteral(StringBuilder literal, List<Node> nodes) {
			if (!literal.isEmpty()) {
				nodes.add(new LiteralNode(literal.toString()));
				literal.setLength(0);
			}
		}

		private Node parseVariable() {
			int start = index;
			int end = findMatchingDelimiter(start + 1, '{', '}');
			if (end == -1) {
				String raw = source.substring(start);
				index = source.length();
				return new LiteralNode(raw);
			}

			String rawPlaceholder = source.substring(start, end + 1);
			index = end + 1;

			String inner = source.substring(start + 1, end);
			List<String> parts = Modifiers.splitUnescaped(inner, ':');
			if (parts.isEmpty()) {
				return new LiteralNode(rawPlaceholder);
			}

			String variableKey = parts.getFirst().trim();
			Variable<?> variable = variableResolver.apply(variableKey);
			if (variable == null) {
				return new LiteralNode(rawPlaceholder);
			}

			List<Modifiers.ResolvedModifier<?, ?>> modifiers = new ArrayList<>(Math.max(0, parts.size() - 1));
			for (int i = 1; i < parts.size(); i++) {
				Modifiers.ResolvedModifier<?, ?> resolvedModifier = Modifiers.get(parts.get(i));
				if (resolvedModifier == null) {
					return new LiteralNode(rawPlaceholder);
				}
				modifiers.add(resolvedModifier);
			}

			return new VariableNode(rawPlaceholder, variableKey, variable, modifiers);
		}

		private Node parseDirective() {
			int start = index;

			ColorDirective hexDirective = parseHexDirective(start);
			if (hexDirective != null) {
				index += 8;
				hasExplicitColors = true;
				return new DirectiveNode(hexDirective);
			}

			NamedDirective namedDirective = findNamedDirective(start + 1);
			if (namedDirective != null) {
				index += 1 + namedDirective.key().length();
				hasExplicitColors |= namedDirective.directive() instanceof ColorDirective;
				hasDynamicColors |= namedDirective.directive() instanceof ColorDirective colorDirective
						&& colorDirective.kind() == ColorDirectiveKind.CHROMA;
				return new DirectiveNode(namedDirective.directive());
			}

			if (start + 1 < source.length()) {
				Directive singleCharDirective = SINGLE_CHAR_DIRECTIVES.get(source.charAt(start + 1));
				if (singleCharDirective != null && hasSingleCharBoundary(start + 2)) {
					index += 2;
					hasExplicitColors |= singleCharDirective instanceof ColorDirective;
					hasDynamicColors |= singleCharDirective instanceof ColorDirective colorDirective
							&& colorDirective.kind() == ColorDirectiveKind.CHROMA;
					return new DirectiveNode(singleCharDirective);
				}
			}

			int literalEnd = findDirectiveLiteralEnd(start);
			String literal = source.substring(start, literalEnd);
			index = literalEnd;
			return new LiteralNode(literal);
		}

		private @Nullable ColorDirective parseHexDirective(int start) {
			if (start + 8 > source.length() || source.charAt(start + 1) != '#') {
				return null;
			}

			String hex = source.substring(start + 2, start + 8);
			if (!hex.chars().allMatch(character -> Character.digit(character, 16) != -1)) {
				return null;
			}

			return new ColorDirective(ColorDirectiveKind.STATIC, Integer.parseInt(hex, 16));
		}

		private @Nullable NamedDirective findNamedDirective(int start) {
			String lowerCaseTail = source.substring(start).toLowerCase();
			for (NamedDirective directive : NAMED_DIRECTIVES_BY_LENGTH) {
				if (lowerCaseTail.startsWith(directive.key())) {
					return directive;
				}
			}

			return null;
		}

		private boolean hasSingleCharBoundary(int nextIndex) {
			return nextIndex >= source.length() || !Character.isLowerCase(source.charAt(nextIndex));
		}

		private int findDirectiveLiteralEnd(int start) {
			int cursor = start + 1;
			if (cursor >= source.length()) {
				return source.length();
			}

			if (source.charAt(cursor) == '#') {
				cursor++;
				while (cursor < source.length() && isHexCharacter(source.charAt(cursor))) {
					cursor++;
				}
				return cursor;
			}

			while (cursor < source.length() && isDirectiveTokenCharacter(source.charAt(cursor))) {
				cursor++;
			}

			return Math.max(start + 2, cursor);
		}

		private Node parseGradient() {
			int start = index;
			int commaIndex = findGradientHeaderSeparator(start + 1);
			int gradientEnd = findMatchingDelimiter(start + 1, '[', ']');

			if (commaIndex == -1 || gradientEnd == -1 || commaIndex > gradientEnd) {
				index = gradientEnd == -1 ? source.length() : gradientEnd + 1;
				return new LiteralNode(source.substring(start, index));
			}

			String header = source.substring(start + 1, commaIndex).trim();
			int separatorIndex = header.indexOf(':');
			if (separatorIndex <= 0 || separatorIndex != header.lastIndexOf(':')) {
				index = gradientEnd + 1;
				return new LiteralNode(source.substring(start, index));
			}

			Integer startColor = parseGradientColorSpec(header.substring(0, separatorIndex).trim());
			Integer endColor = parseGradientColorSpec(header.substring(separatorIndex + 1).trim());
			if (startColor == null || endColor == null) {
				index = gradientEnd + 1;
				return new LiteralNode(source.substring(start, index));
			}

			index = commaIndex + 1;
			int contentStart = index;
			SequenceNode content = parseSequence(true);
			if (index >= source.length() || source.charAt(index) != ']') {
				index = gradientEnd + 1;
				return new LiteralNode(source.substring(start, index));
			}

			if (contentStart > gradientEnd) {
				index = gradientEnd + 1;
				return new LiteralNode(source.substring(start, index));
			}

			index++;
			hasExplicitColors = true;
			return new GradientNode(startColor, endColor, content);
		}

		private int findGradientHeaderSeparator(int start) {
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

				if (current == ',') {
					return cursor;
				}

				if (current == ']') {
					return -1;
				}
			}

			return -1;
		}

		private int findMatchingDelimiter(int start, char open, char close) {
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

		private @Nullable Integer parseGradientColorSpec(String rawColor) {
			if (rawColor.startsWith("#") && rawColor.length() == 7 && rawColor.substring(1).chars().allMatch(character -> Character.digit(character, 16) != -1)) {
				return Integer.parseInt(rawColor.substring(1), 16);
			}

			return NAMED_COLORS.get(rawColor.toLowerCase());
		}
	}

	private static void registerNamedColor(String key, int rgb, char singleCharCode) {
		ColorDirective directive = new ColorDirective(ColorDirectiveKind.STATIC, rgb);
		NAMED_COLORS.put(key, rgb);
		registerNamedDirective(key, directive);
		registerSingleCharDirective(singleCharCode, directive);
	}

	private static void registerNamedColorAlias(String alias, String target) {
		Integer rgb = NAMED_COLORS.get(target);
		if (rgb == null) {
			throw new IllegalArgumentException("Unknown target color " + target);
		}

		NAMED_COLORS.put(alias, rgb);
		registerNamedDirective(alias, new ColorDirective(ColorDirectiveKind.STATIC, rgb));
	}

	private static void registerNamedDirective(String key, Directive directive) {
		NAMED_DIRECTIVES.put(key, new NamedDirective(key, directive));
	}

	private static void registerSingleCharDirective(char key, Directive directive) {
		SINGLE_CHAR_DIRECTIVES.put(key, directive);
	}

	private static boolean isDirectiveTokenCharacter(char character) {
		return Character.isLowerCase(character) || Character.isDigit(character) || character == '_';
	}

	private static boolean isHexCharacter(char character) {
		return Character.digit(character, 16) != -1;
	}
}
