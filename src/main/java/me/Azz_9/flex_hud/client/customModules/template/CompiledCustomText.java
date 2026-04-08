package me.Azz_9.flex_hud.client.customModules.template;

import static java.util.Objects.requireNonNull;
import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.Variables;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;
import me.Azz_9.flex_hud.client.tickables.ChromaColorTickable;

public final class CompiledCustomText {

	private static final int DEFAULT_STYLED_TEXT_COLOR = 0xffffff;
	private static final Map<String, NamedDirective> NAMED_DIRECTIVES = new HashMap<>();
	private static final List<NamedDirective> NAMED_DIRECTIVES_BY_LENGTH = new ArrayList<>();
	private static final Map<Character, StyleDirective> SINGLE_CHAR_DIRECTIVES = new HashMap<>();
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

		registerNamedDirective("bold", ToggleStyleDirective.BOLD);
		registerNamedDirective("italic", ToggleStyleDirective.ITALIC);
		registerNamedDirective("underline", ToggleStyleDirective.UNDERLINE);
		registerNamedDirective("underlined", ToggleStyleDirective.UNDERLINE);
		registerNamedDirective("strikethrough", ToggleStyleDirective.STRIKETHROUGH);
		registerNamedDirective("strike", ToggleStyleDirective.STRIKETHROUGH);
		registerNamedDirective("obfuscated", ToggleStyleDirective.OBFUSCATED);
		registerNamedDirective("magic", ToggleStyleDirective.OBFUSCATED);
		registerNamedDirective("reset", ResetDirective.INSTANCE);
		registerNamedDirective("chroma", new ColorDirective(DynamicColorSource.INSTANCE));

		registerSingleCharDirective('k', ToggleStyleDirective.OBFUSCATED);
		registerSingleCharDirective('l', ToggleStyleDirective.BOLD);
		registerSingleCharDirective('m', ToggleStyleDirective.STRIKETHROUGH);
		registerSingleCharDirective('n', ToggleStyleDirective.UNDERLINE);
		registerSingleCharDirective('o', ToggleStyleDirective.ITALIC);
		registerSingleCharDirective('r', ResetDirective.INSTANCE);

		NAMED_DIRECTIVES_BY_LENGTH.addAll(NAMED_DIRECTIVES.values());
		NAMED_DIRECTIVES_BY_LENGTH.sort(Comparator.comparingInt((NamedDirective directive) -> directive.key().length()).reversed());
	}

	private final String source;
	private final SequenceNode root;
	private final List<CompiledVariable> variables;
	private final boolean hasExplicitColors;
	private final boolean hasDynamicColors;

	private @Nullable RenderData cachedRenderData;

	private CompiledCustomText(String source,
	                           SequenceNode root,
	                           List<CompiledVariable> variables,
	                           boolean hasExplicitColors,
	                           boolean hasDynamicColors) {
		this.source = source;
		this.root = root;
		this.variables = List.copyOf(variables);
		this.hasExplicitColors = hasExplicitColors;
		this.hasDynamicColors = hasDynamicColors;
	}

	public static CompiledCustomText compile(String source) {
		return compile(source, Variables::get);
	}

	public static CompiledCustomText compile(String source, Function<String, @Nullable Variable<?>> variableResolver) {
		return new Parser(source, variableResolver).parse();
	}

	public RenderData getRenderData() {
		boolean shouldRebuild = hasDynamicColors || cachedRenderData == null;
		for (CompiledVariable variable : variables) {
			shouldRebuild |= variable.refreshIfNeeded();
		}

		if (shouldRebuild) {
			cachedRenderData = buildRenderData((text, style) -> CLIENT.textRenderer.getWidth(Text.literal(text).setStyle(style)));
		}

		return requireNonNull(cachedRenderData);
	}

	RenderData getRenderDataForTests() {
		for (CompiledVariable variable : variables) {
			variable.refreshIfNeeded();
		}

		return buildRenderData((text, style) -> text.codePointCount(0, text.length()));
	}

	public String getSource() {
		return source;
	}

	private RenderData buildRenderData(WidthMeasurer widthMeasurer) {
		List<StyledGlyph> glyphs = new ArrayList<>();
		root.render(StyleState.EMPTY, glyphs, widthMeasurer);

		List<StyledRun> runs = mergeRuns(glyphs);
		MutableText text = Text.empty();
		int width = 0;
		for (StyledRun run : runs) {
			if (!run.text().isEmpty()) {
				Style minecraftStyle = run.style().toMinecraftStyle(hasExplicitColors);
				text.append(Text.literal(run.text()).setStyle(minecraftStyle));
				width += widthMeasurer.measure(run.text(), run.style().toMeasurementStyle());
			}
		}

		return new RenderData(text, width, hasExplicitColors);
	}

	private static List<StyledRun> mergeRuns(List<StyledGlyph> glyphs) {
		if (glyphs.isEmpty()) {
			return List.of();
		}

		List<StyledRun> runs = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		StyleState currentStyle = glyphs.getFirst().style();

		for (StyledGlyph glyph : glyphs) {
			if (!glyph.style().equals(currentStyle)) {
				runs.add(new StyledRun(builder.toString(), currentStyle));
				builder.setLength(0);
				currentStyle = glyph.style();
			}
			builder.append(glyph.text());
		}

		runs.add(new StyledRun(builder.toString(), currentStyle));
		return runs;
	}

	private static void appendText(String text, StyleState style, List<StyledGlyph> output) {
		text.codePoints()
				.mapToObj(Character::toChars)
				.map(String::new)
				.map(codePoint -> new StyledGlyph(codePoint, style))
				.forEach(output::add);
	}

	private static void applyGradient(List<StyledGlyph> glyphs, int startColor, int endColor, WidthMeasurer widthMeasurer) {
		if (glyphs.isEmpty()) {
			return;
		}

		int totalWidth = 0;
		List<Integer> widths = new ArrayList<>(glyphs.size());
		for (StyledGlyph glyph : glyphs) {
			int width = widthMeasurer.measure(glyph.text(), glyph.style().toMeasurementStyle());
			widths.add(width);
			totalWidth += width;
		}

		double currentX = 0.0;
		for (int i = 0; i < glyphs.size(); i++) {
			StyledGlyph glyph = glyphs.get(i);
			int width = widths.get(i);
			if (glyph.style().colorSource() == GradientPlaceholderColorSource.INSTANCE) {
				float progress = totalWidth <= 0 ? 0.0f : (float) ((currentX + width / 2.0) / totalWidth);
				int gradientColor = interpolateRgb(startColor, endColor, progress);
				glyphs.set(i, glyph.withColorSource(new StaticColorSource(gradientColor)));
			}

			currentX += width;
		}
	}

	private static int interpolateRgb(int startColor, int endColor, float progress) {
		float clampedProgress = Math.clamp(progress, 0.0f, 1.0f);

		int startRed = (startColor >> 16) & 0xff;
		int startGreen = (startColor >> 8) & 0xff;
		int startBlue = startColor & 0xff;

		int endRed = (endColor >> 16) & 0xff;
		int endGreen = (endColor >> 8) & 0xff;
		int endBlue = endColor & 0xff;

		int red = Math.round(startRed + (endRed - startRed) * clampedProgress);
		int green = Math.round(startGreen + (endGreen - startGreen) * clampedProgress);
		int blue = Math.round(startBlue + (endBlue - startBlue) * clampedProgress);

		return red << 16 | green << 8 | blue;
	}

	public record RenderData(Text text, int width, boolean hasOwnColors) {
	}

	private record StyledRun(String text, StyleState style) {
	}

	private record StyledGlyph(String text, StyleState style) {
		private StyledGlyph withColorSource(ColorSource colorSource) {
			return new StyledGlyph(text, style.withColorSource(colorSource));
		}
	}

	private sealed interface Node permits SequenceNode, LiteralNode, VariableNode, DirectiveNode, GradientNode {
		StyleState render(StyleState style, List<StyledGlyph> output, WidthMeasurer widthMeasurer);
	}

	private record SequenceNode(List<Node> children) implements Node {
		@Override
		public StyleState render(StyleState style, List<StyledGlyph> output, WidthMeasurer widthMeasurer) {
			StyleState current = style;
			for (Node child : children) {
				current = child.render(current, output, widthMeasurer);
			}
			return current;
		}
	}

	private record LiteralNode(String text) implements Node {
		@Override
		public StyleState render(StyleState style, List<StyledGlyph> output, WidthMeasurer widthMeasurer) {
			appendText(text, style, output);
			return style;
		}
	}

	private record VariableNode(CompiledVariable variable) implements Node {
		@Override
		public StyleState render(StyleState style, List<StyledGlyph> output, WidthMeasurer widthMeasurer) {
			appendText(variable.getFormattedValue(), style, output);
			return style;
		}
	}

	private record DirectiveNode(StyleDirective directive) implements Node {
		@Override
		public StyleState render(StyleState style, List<StyledGlyph> output, WidthMeasurer widthMeasurer) {
			return directive.apply(style);
		}
	}

	private record GradientNode(int startColor, int endColor, SequenceNode content) implements Node {
		@Override
		public StyleState render(StyleState style, List<StyledGlyph> output, WidthMeasurer widthMeasurer) {
			List<StyledGlyph> gradientGlyphs = new ArrayList<>();
			content.render(style.withColorSource(GradientPlaceholderColorSource.INSTANCE), gradientGlyphs, widthMeasurer);
			applyGradient(gradientGlyphs, startColor, endColor, widthMeasurer);
			output.addAll(gradientGlyphs);
			return style;
		}
	}

	private record StyleState(boolean bold,
	                          boolean italic,
	                          boolean underline,
	                          boolean strikethrough,
	                          boolean obfuscated,
	                          ColorSource colorSource) {

		private static final StyleState EMPTY = new StyleState(
				false, false, false, false, false, DefaultColorSource.INSTANCE
		);

		private StyleState withBold(boolean newValue) {
			return new StyleState(newValue, italic, underline, strikethrough, obfuscated, colorSource);
		}

		private StyleState withItalic(boolean newValue) {
			return new StyleState(bold, newValue, underline, strikethrough, obfuscated, colorSource);
		}

		private StyleState withUnderline(boolean newValue) {
			return new StyleState(bold, italic, newValue, strikethrough, obfuscated, colorSource);
		}

		private StyleState withStrikethrough(boolean newValue) {
			return new StyleState(bold, italic, underline, newValue, obfuscated, colorSource);
		}

		private StyleState withObfuscated(boolean newValue) {
			return new StyleState(bold, italic, underline, strikethrough, newValue, colorSource);
		}

		private StyleState withColorSource(ColorSource newColorSource) {
			return new StyleState(bold, italic, underline, strikethrough, obfuscated, newColorSource);
		}

		private StyleState withoutColorSource() {
			return withColorSource(DefaultColorSource.INSTANCE);
		}

		private StyleState reset() {
			return EMPTY;
		}

		private Style toMinecraftStyle(boolean forceDefaultColor) {
			Style style = Style.EMPTY
					.withBold(bold)
					.withItalic(italic)
					.withUnderline(underline)
					.withStrikethrough(strikethrough)
					.withObfuscated(obfuscated);

			Integer rgb = colorSource.resolveRgb(forceDefaultColor);
			if (rgb != null) {
				style = style.withColor(TextColor.fromRgb(rgb));
			}

			return style;
		}

		private Style toMeasurementStyle() {
			return withoutColorSource().toMinecraftStyle(false);
		}
	}

	private sealed interface ColorSource permits DefaultColorSource, StaticColorSource, DynamicColorSource, GradientPlaceholderColorSource {
		@Nullable Integer resolveRgb(boolean forceDefaultColor);
	}

	private enum DefaultColorSource implements ColorSource {
		INSTANCE;

		@Override
		public @Nullable Integer resolveRgb(boolean forceDefaultColor) {
			return forceDefaultColor ? DEFAULT_STYLED_TEXT_COLOR : null;
		}
	}

	private record StaticColorSource(int rgb) implements ColorSource {
		@Override
		public Integer resolveRgb(boolean forceDefaultColor) {
			return rgb;
		}
	}

	private enum DynamicColorSource implements ColorSource {
		INSTANCE;

		@Override
		public Integer resolveRgb(boolean forceDefaultColor) {
			return ChromaColorTickable.getColor() & 0x00ffffff;
		}
	}

	private enum GradientPlaceholderColorSource implements ColorSource {
		INSTANCE;

		@Override
		public @Nullable Integer resolveRgb(boolean forceDefaultColor) {
			throw new IllegalStateException("Gradient placeholders must be resolved before rendering.");
		}
	}

	private sealed interface StyleDirective permits ToggleStyleDirective, ColorDirective, ResetDirective {
		StyleState apply(StyleState style);

		default boolean isColorDirective() {
			return false;
		}

		default boolean isDynamicColorDirective() {
			return false;
		}
	}

	private enum ToggleStyleDirective implements StyleDirective {
		BOLD {
			@Override
			public StyleState apply(StyleState style) {
				return style.withBold(!style.bold());
			}
		},
		ITALIC {
			@Override
			public StyleState apply(StyleState style) {
				return style.withItalic(!style.italic());
			}
		},
		UNDERLINE {
			@Override
			public StyleState apply(StyleState style) {
				return style.withUnderline(!style.underline());
			}
		},
		STRIKETHROUGH {
			@Override
			public StyleState apply(StyleState style) {
				return style.withStrikethrough(!style.strikethrough());
			}
		},
		OBFUSCATED {
			@Override
			public StyleState apply(StyleState style) {
				return style.withObfuscated(!style.obfuscated());
			}
		}
	}

	private record ColorDirective(ColorSource colorSource) implements StyleDirective {
		@Override
		public StyleState apply(StyleState style) {
			return style.withColorSource(colorSource);
		}

		@Override
		public boolean isColorDirective() {
			return true;
		}

		@Override
		public boolean isDynamicColorDirective() {
			return colorSource == DynamicColorSource.INSTANCE;
		}
	}

	private enum ResetDirective implements StyleDirective {
		INSTANCE;

		@Override
		public StyleState apply(StyleState style) {
			return style.reset();
		}
	}

	private record NamedDirective(String key, StyleDirective directive) {
	}

	private static final class CompiledVariable {
		private final String rawPlaceholder;
		private final Variable<?> variable;
		private final List<Modifiers.ResolvedModifier<?, ?>> modifiers;

		private Modifiers.@Nullable CompiledFormatter formatter;
		private @Nullable Class<?> formatterInputType;
		private boolean formatterInvalid;
		private long lastSeenVersion = Long.MIN_VALUE;
		private String formattedValue = "";

		private CompiledVariable(String rawPlaceholder, Variable<?> variable, List<Modifiers.ResolvedModifier<?, ?>> modifiers) {
			this.rawPlaceholder = rawPlaceholder;
			this.variable = variable;
			this.modifiers = List.copyOf(modifiers);
		}

		private boolean refreshIfNeeded() {
			Object currentValue = variable.getValue();
			Class<?> inputType = currentValue != null ? currentValue.getClass() : null;

			if (!Objects.equals(formatterInputType, inputType)) {
				formatterInputType = inputType;
				formatter = Modifiers.compileFormatter(inputType, modifiers);
				formatterInvalid = formatter == null && !modifiers.isEmpty();
			}

			if (lastSeenVersion == variable.getVersion() && !(formatterInvalid && !rawPlaceholder.equals(formattedValue))) {
				return false;
			}

			String newFormattedValue;
			if (formatterInvalid) {
				newFormattedValue = rawPlaceholder;
			} else if (formatter != null) {
				newFormattedValue = formatter.format(currentValue);
			} else {
				newFormattedValue = String.valueOf(currentValue);
			}

			lastSeenVersion = variable.getVersion();
			if (formattedValue.equals(newFormattedValue)) {
				return false;
			}

			formattedValue = newFormattedValue;
			return true;
		}

		private String getFormattedValue() {
			return formattedValue;
		}
	}

	private static final class Parser {
		private final String source;
		private final Function<String, @Nullable Variable<?>> variableResolver;
		private final List<CompiledVariable> variables = new ArrayList<>();

		private int index;
		private boolean hasExplicitColors;
		private boolean hasDynamicColors;

		private Parser(String source, Function<String, @Nullable Variable<?>> variableResolver) {
			this.source = requireNonNull(source, "source");
			this.variableResolver = requireNonNull(variableResolver, "variableResolver");
		}

		private CompiledCustomText parse() {
			SequenceNode root = parseSequence(false);
			return new CompiledCustomText(source, root, variables, hasExplicitColors, hasDynamicColors);
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
			return new SequenceNode(List.copyOf(nodes));
		}

		private void flushLiteral(StringBuilder literal, List<Node> nodes) {
			if (literal.length() > 0) {
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
			String[] parts = inner.split(":", -1);
			if (parts.length == 0) {
				return new LiteralNode(rawPlaceholder);
			}

			Variable<?> variable = variableResolver.apply(parts[0].trim());
			if (variable == null) {
				return new LiteralNode(rawPlaceholder);
			}

			List<Modifiers.ResolvedModifier<?, ?>> modifiers = new ArrayList<>(Math.max(0, parts.length - 1));
			for (int i = 1; i < parts.length; i++) {
				Modifiers.ResolvedModifier<?, ?> resolvedModifier = Modifiers.get(parts[i].trim());
				if (resolvedModifier == null) {
					return new LiteralNode(rawPlaceholder);
				}
				modifiers.add(resolvedModifier);
			}

			CompiledVariable compiledVariable = new CompiledVariable(rawPlaceholder, variable, modifiers);
			compiledVariable.refreshIfNeeded();
			variables.add(compiledVariable);
			return new VariableNode(compiledVariable);
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
				hasExplicitColors |= namedDirective.directive().isColorDirective();
				hasDynamicColors |= namedDirective.directive().isDynamicColorDirective();
				return new DirectiveNode(namedDirective.directive());
			}

			if (start + 1 < source.length()) {
				StyleDirective singleCharDirective = SINGLE_CHAR_DIRECTIVES.get(source.charAt(start + 1));
				if (singleCharDirective != null && hasSingleCharBoundary(start + 2)) {
					index += 2;
					hasExplicitColors |= singleCharDirective.isColorDirective();
					hasDynamicColors |= singleCharDirective.isDynamicColorDirective();
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

			return new ColorDirective(new StaticColorSource(Integer.parseInt(hex, 16)));
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
		ColorDirective directive = new ColorDirective(new StaticColorSource(rgb));
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
		registerNamedDirective(alias, new ColorDirective(new StaticColorSource(rgb)));
	}

	private static void registerNamedDirective(String key, StyleDirective directive) {
		NAMED_DIRECTIVES.put(key, new NamedDirective(key, directive));
	}

	private static void registerSingleCharDirective(char key, StyleDirective directive) {
		SINGLE_CHAR_DIRECTIVES.put(key, directive);
	}

	private static boolean isDirectiveTokenCharacter(char character) {
		return Character.isLowerCase(character) || Character.isDigit(character) || character == '_';
	}

	private static boolean isHexCharacter(char character) {
		return Character.digit(character, 16) != -1;
	}

	@FunctionalInterface
	private interface WidthMeasurer {
		int measure(String text, Style style);
	}
}
