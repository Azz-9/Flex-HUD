package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static java.util.Objects.requireNonNull;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.Variables;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;

public final class ModuleContentEditorModel {

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
		registerNamedDirective("chroma", new ColorDirective(ChromaColorLayer.INSTANCE));

		registerSingleCharDirective('k', ToggleStyleDirective.OBFUSCATED);
		registerSingleCharDirective('l', ToggleStyleDirective.BOLD);
		registerSingleCharDirective('m', ToggleStyleDirective.STRIKETHROUGH);
		registerSingleCharDirective('n', ToggleStyleDirective.UNDERLINE);
		registerSingleCharDirective('o', ToggleStyleDirective.ITALIC);
		registerSingleCharDirective('r', ResetDirective.INSTANCE);

		NAMED_DIRECTIVES_BY_LENGTH.addAll(NAMED_DIRECTIVES.values());
		NAMED_DIRECTIVES_BY_LENGTH.sort(Comparator.comparingInt((NamedDirective directive) -> directive.key().length()).reversed());
	}

	private final List<InlineElement> elements;

	private ModuleContentEditorModel(List<InlineElement> elements) {
		this.elements = elements;
	}

	public static ModuleContentEditorModel empty() {
		return new ModuleContentEditorModel(new ArrayList<>());
	}

	public static ModuleContentEditorModel parse(String rawText) {
		return parse(rawText, StyleState.EMPTY);
	}

	public static ModuleContentEditorModel parse(String rawText, StyleState initialStyle) {
		return new Parser(rawText, Variables::get, initialStyle).parse();
	}

	public ModuleContentEditorModel copy() {
		List<InlineElement> copies = new ArrayList<>(elements.size());
		for (InlineElement element : elements) {
			copies.add(element.copy());
		}
		return new ModuleContentEditorModel(copies);
	}

	public int size() {
		return elements.size();
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public InlineElement get(int index) {
		return elements.get(index);
	}

	public List<InlineElement> elements() {
		return List.copyOf(elements);
	}

	public void insertModel(int index, ModuleContentEditorModel model) {
		int insertionIndex = Math.clamp(index, 0, elements.size());
		for (InlineElement element : model.elements) {
			elements.add(insertionIndex++, element.copy());
		}
	}

	public void insertText(int index, String text, StyleState style) {
		int insertionIndex = Math.clamp(index, 0, elements.size());
		List<InlineElement> inserted = new ArrayList<>();
		text.codePoints()
				.mapToObj(Character::toChars)
				.map(String::new)
				.forEach(character -> inserted.add(new TextElement(character, style)));
		elements.addAll(insertionIndex, inserted);
	}

	public void insertVariable(int index, Variable<?> variable, StyleState style) {
		elements.add(Math.clamp(index, 0, elements.size()), new VariableElement(variable.getKey(), variable, new ArrayList<>(), style));
	}

	public void deleteRange(int start, int end) {
		if (start >= end) {
			return;
		}

		elements.subList(Math.clamp(start, 0, elements.size()), Math.clamp(end, 0, elements.size())).clear();
	}

	public ModuleContentEditorModel copyRange(int start, int end) {
		int clampedStart = Math.clamp(start, 0, elements.size());
		int clampedEnd = Math.clamp(end, clampedStart, elements.size());
		List<InlineElement> copies = new ArrayList<>(clampedEnd - clampedStart);
		for (int index = clampedStart; index < clampedEnd; index++) {
			copies.add(elements.get(index).copy());
		}
		return new ModuleContentEditorModel(copies);
	}

	public StyleState getInsertionStyle(int index) {
		if (!elements.isEmpty()) {
			if (index < elements.size()) {
				return elements.get(Math.max(0, index)).style();
			}
			return elements.getLast().style();
		}

		return StyleState.EMPTY;
	}

	public void setStyleFlag(int start, int end, StyleFlag styleFlag, boolean enabled) {
		forEachElement(start, end, element -> element.setStyle(styleFlag.apply(element.style(), enabled)));
	}

	public void replaceColorLayers(int start, int end, List<ColorLayer> colorLayers) {
		forEachElement(start, end, element -> element.setStyle(element.style().withColorLayers(colorLayers)));
	}

	public void updateVariableModifiers(int elementIndex, List<Modifiers.ResolvedModifier<?, ?>> modifiers) {
		if (0 <= elementIndex && elementIndex < elements.size() && elements.get(elementIndex) instanceof VariableElement variableElement) {
			variableElement.setModifiers(modifiers);
		}
	}

	public SelectionSummary summarize(int start, int end) {
		if (start >= end || elements.isEmpty()) {
			return SelectionSummary.EMPTY;
		}

		SelectionSummary.Builder builder = new SelectionSummary.Builder();
		for (int index = Math.max(0, start); index < Math.min(end, elements.size()); index++) {
			builder.accept(elements.get(index).style());
		}
		return builder.build();
	}

	public String serialize() {
		return serializeNodes(buildColorNodes(elements, 0), BooleanState.EMPTY);
	}

	private void forEachElement(int start, int end, java.util.function.Consumer<InlineElement> consumer) {
		for (int index = Math.max(0, start); index < Math.min(end, elements.size()); index++) {
			consumer.accept(elements.get(index));
		}
	}

	private static List<ColorNode> buildColorNodes(List<InlineElement> elements, int depth) {
		List<ColorNode> nodes = new ArrayList<>();
		int index = 0;

		while (index < elements.size()) {
			List<ColorLayer> stack = elements.get(index).style().colorLayers();
			if (stack.size() > depth) {
				ColorLayer layer = stack.get(depth);
				int end = index + 1;
				while (end < elements.size()) {
					List<ColorLayer> nextStack = elements.get(end).style().colorLayers();
					if (nextStack.size() <= depth || !Objects.equals(nextStack.get(depth), layer)) {
						break;
					}
					end++;
				}

				nodes.add(new WrapperNode(layer, buildColorNodes(elements.subList(index, end), depth + 1)));
				index = end;
				continue;
			}

			int end = index + 1;
			while (end < elements.size() && elements.get(end).style().colorLayers().size() <= depth) {
				end++;
			}

			nodes.add(new LeafNode(List.copyOf(elements.subList(index, end))));
			index = end;
		}

		return List.copyOf(nodes);
	}

	private static String serializeNodes(List<ColorNode> nodes, BooleanState baseState) {
		StringBuilder builder = new StringBuilder();
		BooleanState current = baseState;

		for (ColorNode node : nodes) {
			if (node instanceof LeafNode leafNode) {
				current = serializeLeaf(leafNode.elements(), current, builder);
				continue;
			}

			WrapperNode wrapperNode = (WrapperNode) node;
			builder.append(openColorLayer(wrapperNode.layer()));
			BooleanState wrapperEndState = serializeLeafAndWrappers(wrapperNode.children(), current, builder);
			builder.append(diffBooleanState(wrapperEndState, current));
			builder.append(closeColorLayer(wrapperNode.layer()));
		}

		return builder.toString();
	}

	private static BooleanState serializeLeafAndWrappers(List<ColorNode> nodes, BooleanState initialState, StringBuilder builder) {
		BooleanState current = initialState;
		for (ColorNode node : nodes) {
			if (node instanceof LeafNode leafNode) {
				current = serializeLeaf(leafNode.elements(), current, builder);
				continue;
			}

			WrapperNode wrapperNode = (WrapperNode) node;
			builder.append(openColorLayer(wrapperNode.layer()));
			BooleanState wrapperEndState = serializeLeafAndWrappers(wrapperNode.children(), current, builder);
			builder.append(diffBooleanState(wrapperEndState, current));
			builder.append(closeColorLayer(wrapperNode.layer()));
		}
		return current;
	}

	private static BooleanState serializeLeaf(List<InlineElement> elements, BooleanState initialState, StringBuilder builder) {
		BooleanState current = initialState;
		for (InlineElement element : elements) {
			BooleanState targetState = element.style().booleanState();
			builder.append(diffBooleanState(current, targetState));
			builder.append(serializeElement(element));
			current = targetState;
		}
		return current;
	}

	private static String diffBooleanState(BooleanState from, BooleanState to) {
		StringBuilder builder = new StringBuilder();
		appendStyleToggle(builder, from.bold(), to.bold(), "bold");
		appendStyleToggle(builder, from.italic(), to.italic(), "italic");
		appendStyleToggle(builder, from.underline(), to.underline(), "underline");
		appendStyleToggle(builder, from.strikethrough(), to.strikethrough(), "strikethrough");
		appendStyleToggle(builder, from.obfuscated(), to.obfuscated(), "obfuscated");
		return builder.toString();
	}

	private static void appendStyleToggle(StringBuilder builder, boolean current, boolean target, String directive) {
		if (current != target) {
			builder.append('&').append(directive);
		}
	}

	private static String serializeElement(InlineElement element) {
		if (element instanceof TextElement textElement) {
			return escapeLiteral(textElement.text());
		}

		VariableElement variableElement = (VariableElement) element;
		StringBuilder builder = new StringBuilder();
		builder.append('{').append(variableElement.key());
		for (Modifiers.ResolvedModifier<?, ?> modifier : variableElement.modifiers()) {
			builder.append(':').append(Modifiers.formatRaw(modifier));
		}
		builder.append('}');
		return builder.toString();
	}

	private static String escapeLiteral(String text) {
		StringBuilder escaped = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++) {
			char character = text.charAt(i);
			if (character == '\\' || character == '{' || character == '}' || character == '[' || character == ']' || character == '&') {
				escaped.append('\\');
			}
			escaped.append(character);
		}
		return escaped.toString();
	}

	private static String openColorLayer(ColorLayer colorLayer) {
		if (colorLayer instanceof StaticColorLayer staticColorLayer) {
			return "&#" + String.format("%06x", staticColorLayer.rgb());
		}
		if (colorLayer == ChromaColorLayer.INSTANCE) {
			return "&chroma";
		}

		GradientColorLayer gradientColorLayer = (GradientColorLayer) colorLayer;
		return "[#%06x:#%06x,".formatted(gradientColorLayer.startColor(), gradientColorLayer.endColor());
	}

	private static String closeColorLayer(ColorLayer colorLayer) {
		if (colorLayer instanceof GradientColorLayer) {
			return "]";
		}

		return openColorLayer(colorLayer);
	}

	public enum StyleFlag {
		BOLD {
			@Override
			StyleState apply(StyleState style, boolean enabled) {
				return style.withBold(enabled);
			}
		},
		ITALIC {
			@Override
			StyleState apply(StyleState style, boolean enabled) {
				return style.withItalic(enabled);
			}
		},
		UNDERLINE {
			@Override
			StyleState apply(StyleState style, boolean enabled) {
				return style.withUnderline(enabled);
			}
		},
		STRIKETHROUGH {
			@Override
			StyleState apply(StyleState style, boolean enabled) {
				return style.withStrikethrough(enabled);
			}
		},
		OBFUSCATED {
			@Override
			StyleState apply(StyleState style, boolean enabled) {
				return style.withObfuscated(enabled);
			}
		};

		abstract StyleState apply(StyleState style, boolean enabled);
	}

	public sealed interface ColorLayer permits StaticColorLayer, ChromaColorLayer, GradientColorLayer {
	}

	public record StaticColorLayer(int rgb) implements ColorLayer {
	}

	public enum ChromaColorLayer implements ColorLayer {
		INSTANCE
	}

	public static final class GradientColorLayer implements ColorLayer {
		private final int startColor;
		private final int endColor;

		public GradientColorLayer(int startColor, int endColor) {
			this.startColor = startColor;
			this.endColor = endColor;
		}

		public int startColor() {
			return startColor;
		}

		public int endColor() {
			return endColor;
		}
	}

	public record StyleState(boolean bold,
	                         boolean italic,
	                         boolean underline,
	                         boolean strikethrough,
	                         boolean obfuscated,
	                         List<ColorLayer> colorLayers) {
		public static final StyleState EMPTY = new StyleState(false, false, false, false, false, List.of());

		public StyleState {
			colorLayers = List.copyOf(colorLayers);
		}

		public StyleState withBold(boolean bold) {
			return new StyleState(bold, italic, underline, strikethrough, obfuscated, colorLayers);
		}

		public StyleState withItalic(boolean italic) {
			return new StyleState(bold, italic, underline, strikethrough, obfuscated, colorLayers);
		}

		public StyleState withUnderline(boolean underline) {
			return new StyleState(bold, italic, underline, strikethrough, obfuscated, colorLayers);
		}

		public StyleState withStrikethrough(boolean strikethrough) {
			return new StyleState(bold, italic, underline, strikethrough, obfuscated, colorLayers);
		}

		public StyleState withObfuscated(boolean obfuscated) {
			return new StyleState(bold, italic, underline, strikethrough, obfuscated, colorLayers);
		}

		public StyleState pushColorLayer(ColorLayer colorLayer) {
			List<ColorLayer> newStack = new ArrayList<>(colorLayers);
			newStack.add(colorLayer);
			return new StyleState(bold, italic, underline, strikethrough, obfuscated, newStack);
		}

		public StyleState toggleColorLayer(ColorLayer colorLayer) {
			if (!colorLayers.isEmpty() && Objects.equals(colorLayers.getLast(), colorLayer)) {
				return new StyleState(bold, italic, underline, strikethrough, obfuscated, colorLayers.subList(0, colorLayers.size() - 1));
			}
			return pushColorLayer(colorLayer);
		}

		public StyleState reset() {
			return EMPTY;
		}

		public StyleState withColorLayers(List<ColorLayer> colorLayers) {
			return new StyleState(bold, italic, underline, strikethrough, obfuscated, colorLayers);
		}

		public BooleanState booleanState() {
			return new BooleanState(bold, italic, underline, strikethrough, obfuscated);
		}
	}

	public record BooleanState(boolean bold,
	                           boolean italic,
	                           boolean underline,
	                           boolean strikethrough,
	                           boolean obfuscated) {
		public static final BooleanState EMPTY = new BooleanState(false, false, false, false, false);
	}

	public sealed static abstract class InlineElement permits TextElement, VariableElement {
		private StyleState style;

		protected InlineElement(StyleState style) {
			this.style = requireNonNull(style, "style");
		}

		public StyleState style() {
			return style;
		}

		public void setStyle(StyleState style) {
			this.style = requireNonNull(style, "style");
		}

		public abstract InlineElement copy();

		public abstract String visibleText();
	}

	public static final class TextElement extends InlineElement {
		private final String text;

		public TextElement(String text, StyleState style) {
			super(style);
			this.text = requireNonNull(text, "text");
		}

		public String text() {
			return text;
		}

		@Override
		public InlineElement copy() {
			return new TextElement(text, style());
		}

		@Override
		public String visibleText() {
			return text;
		}
	}

	public static final class VariableElement extends InlineElement {
		private final String key;
		private final Variable<?> variable;
		private List<Modifiers.ResolvedModifier<?, ?>> modifiers;

		public VariableElement(String key, Variable<?> variable, List<Modifiers.ResolvedModifier<?, ?>> modifiers, StyleState style) {
			super(style);
			this.key = requireNonNull(key, "key");
			this.variable = requireNonNull(variable, "variable");
			this.modifiers = new ArrayList<>(modifiers);
		}

		public String key() {
			return key;
		}

		public Variable<?> variable() {
			return variable;
		}

		public List<Modifiers.ResolvedModifier<?, ?>> modifiers() {
			return List.copyOf(modifiers);
		}

		public void setModifiers(List<Modifiers.ResolvedModifier<?, ?>> modifiers) {
			this.modifiers = new ArrayList<>(modifiers);
		}

		@Override
		public InlineElement copy() {
			return new VariableElement(key, variable, modifiers, style());
		}

		@Override
		public String visibleText() {
			return variable.getName().getString();
		}
	}

	public record SelectionSummary(TriState bold,
	                               TriState italic,
	                               TriState underline,
	                               TriState strikethrough,
	                               TriState obfuscated,
	                               ColorSummary colorSummary) {
		public static final SelectionSummary EMPTY = new SelectionSummary(
				TriState.OFF, TriState.OFF, TriState.OFF, TriState.OFF, TriState.OFF, ColorSummary.none()
		);

		static final class Builder {
			private Boolean bold;
			private Boolean italic;
			private Boolean underline;
			private Boolean strikethrough;
			private Boolean obfuscated;
			private @Nullable List<ColorLayer> colorLayers;
			private boolean mixedColors;

			private void accept(StyleState style) {
				bold = mergeBooleanState(bold, style.bold());
				italic = mergeBooleanState(italic, style.italic());
				underline = mergeBooleanState(underline, style.underline());
				strikethrough = mergeBooleanState(strikethrough, style.strikethrough());
				obfuscated = mergeBooleanState(obfuscated, style.obfuscated());

				if (colorLayers == null) {
					colorLayers = style.colorLayers();
				} else if (!colorLayers.equals(style.colorLayers())) {
					mixedColors = true;
				}
			}

			private SelectionSummary build() {
				return new SelectionSummary(
						toTriState(bold),
						toTriState(italic),
						toTriState(underline),
						toTriState(strikethrough),
						toTriState(obfuscated),
						buildColorSummary()
				);
			}

			private ColorSummary buildColorSummary() {
				if (mixedColors) {
					return ColorSummary.mixed();
				}
				if (colorLayers == null || colorLayers.isEmpty()) {
					return ColorSummary.none();
				}

				ColorLayer topLayer = colorLayers.getLast();
				if (topLayer instanceof StaticColorLayer staticColorLayer) {
					return ColorSummary.staticColor(staticColorLayer.rgb());
				}
				if (topLayer == ChromaColorLayer.INSTANCE) {
					return ColorSummary.chroma();
				}

				GradientColorLayer gradientColorLayer = (GradientColorLayer) topLayer;
				return ColorSummary.gradient(gradientColorLayer.startColor(), gradientColorLayer.endColor());
			}

			private static @Nullable Boolean mergeBooleanState(@Nullable Boolean current, boolean next) {
				if (current == null) {
					return next;
				}
				if (current == next) {
					return current;
				}
				return null;
			}

			private static TriState toTriState(@Nullable Boolean state) {
				if (state == null) {
					return TriState.MIXED;
				}
				return state ? TriState.ON : TriState.OFF;
			}
		}
	}

	public record ColorSummary(ColorSummaryKind kind,
	                           @Nullable Integer primaryColor,
	                           @Nullable Integer secondaryColor) {
		public static ColorSummary none() {
			return new ColorSummary(ColorSummaryKind.NONE, null, null);
		}

		public static ColorSummary mixed() {
			return new ColorSummary(ColorSummaryKind.MIXED, null, null);
		}

		public static ColorSummary chroma() {
			return new ColorSummary(ColorSummaryKind.CHROMA, null, null);
		}

		public static ColorSummary staticColor(int color) {
			return new ColorSummary(ColorSummaryKind.STATIC, color, null);
		}

		public static ColorSummary gradient(int startColor, int endColor) {
			return new ColorSummary(ColorSummaryKind.GRADIENT, startColor, endColor);
		}
	}

	public enum ColorSummaryKind {
		NONE,
		STATIC,
		CHROMA,
		GRADIENT,
		MIXED
	}

	public enum TriState {
		OFF,
		ON,
		MIXED
	}

	private sealed interface ColorNode permits LeafNode, WrapperNode {
	}

	private record LeafNode(List<InlineElement> elements) implements ColorNode {
	}

	private record WrapperNode(ColorLayer layer, List<ColorNode> children) implements ColorNode {
	}

	private sealed interface StyleDirective permits ToggleStyleDirective, ColorDirective, ResetDirective {
		StyleState apply(StyleState style);
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

	private record ColorDirective(ColorLayer colorLayer) implements StyleDirective {
		@Override
		public StyleState apply(StyleState style) {
			return style.toggleColorLayer(colorLayer);
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

	private static void registerNamedColor(String key, int rgb, char singleCharCode) {
		ColorDirective directive = new ColorDirective(new StaticColorLayer(rgb));
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
		registerNamedDirective(alias, new ColorDirective(new StaticColorLayer(rgb)));
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

	private static final class Parser {
		private final String source;
		private final Function<String, @Nullable Variable<?>> variableResolver;
		private final StyleState initialStyle;

		private int index;

		private Parser(String source, Function<String, @Nullable Variable<?>> variableResolver, StyleState initialStyle) {
			this.source = requireNonNull(source, "source");
			this.variableResolver = requireNonNull(variableResolver, "variableResolver");
			this.initialStyle = requireNonNull(initialStyle, "initialStyle");
		}

		private ModuleContentEditorModel parse() {
			List<InlineElement> elements = new ArrayList<>();
			parseSequence(false, initialStyle, elements);
			return new ModuleContentEditorModel(elements);
		}

		private StyleState parseSequence(boolean stopAtRightBracket, StyleState style, List<InlineElement> output) {
			StyleState current = style;

			while (index < source.length()) {
				char character = source.charAt(index);

				if (stopAtRightBracket && character == ']') {
					return current;
				}

				if (character == '\\') {
					if (index + 1 < source.length()) {
						appendText(String.valueOf(source.charAt(index + 1)), current, output);
						index += 2;
					} else {
						appendText("\\", current, output);
						index++;
					}
					continue;
				}

				if (character == '{') {
					current = parseVariable(current, output);
					continue;
				}

				if (character == '&') {
					current = parseDirective(current, output);
					continue;
				}

				if (character == '[') {
					current = parseGradient(current, output);
					continue;
				}

				appendText(String.valueOf(character), current, output);
				index++;
			}

			return current;
		}

		private StyleState parseVariable(StyleState style, List<InlineElement> output) {
			int start = index;
			int end = findMatchingDelimiter(start + 1, '{', '}');
			if (end == -1) {
				appendRawLiteral(source.substring(start), style, output);
				index = source.length();
				return style;
			}

			String rawPlaceholder = source.substring(start, end + 1);
			index = end + 1;

			String inner = source.substring(start + 1, end);
			List<String> parts = Modifiers.splitUnescaped(inner, ':');
			if (parts.isEmpty()) {
				appendRawLiteral(rawPlaceholder, style, output);
				return style;
			}

			String variableKey = parts.getFirst().trim();
			Variable<?> variable = variableResolver.apply(variableKey);
			if (variable == null) {
				appendRawLiteral(rawPlaceholder, style, output);
				return style;
			}

			List<Modifiers.ResolvedModifier<?, ?>> modifiers = new ArrayList<>();
			for (int i = 1; i < parts.size(); i++) {
				Modifiers.ResolvedModifier<?, ?> resolvedModifier = Modifiers.get(parts.get(i));
				if (resolvedModifier == null) {
					appendRawLiteral(rawPlaceholder, style, output);
					return style;
				}
				modifiers.add(resolvedModifier);
			}

			output.add(new VariableElement(variableKey, variable, modifiers, style));
			return style;
		}

		private StyleState parseDirective(StyleState style, List<InlineElement> output) {
			int start = index;
			ColorDirective hexDirective = parseHexDirective(start);
			if (hexDirective != null) {
				index += 8;
				return hexDirective.apply(style);
			}

			NamedDirective namedDirective = findNamedDirective(start + 1);
			if (namedDirective != null) {
				index += 1 + namedDirective.key().length();
				return namedDirective.directive().apply(style);
			}

			if (start + 1 < source.length()) {
				StyleDirective singleCharDirective = SINGLE_CHAR_DIRECTIVES.get(source.charAt(start + 1));
				if (singleCharDirective != null && hasSingleCharBoundary(start + 2)) {
					index += 2;
					return singleCharDirective.apply(style);
				}
			}

			int literalEnd = findDirectiveLiteralEnd(start);
			appendRawLiteral(source.substring(start, literalEnd), style, output);
			index = literalEnd;
			return style;
		}

		private StyleState parseGradient(StyleState style, List<InlineElement> output) {
			int start = index;
			int commaIndex = findGradientHeaderSeparator(start + 1);
			int gradientEnd = findMatchingDelimiter(start + 1, '[', ']');
			if (commaIndex == -1 || gradientEnd == -1 || commaIndex > gradientEnd) {
				int literalEnd = gradientEnd == -1 ? source.length() : gradientEnd + 1;
				appendRawLiteral(source.substring(start, literalEnd), style, output);
				index = literalEnd;
				return style;
			}

			String header = source.substring(start + 1, commaIndex).trim();
			int separatorIndex = header.indexOf(':');
			if (separatorIndex <= 0 || separatorIndex != header.lastIndexOf(':')) {
				appendRawLiteral(source.substring(start, gradientEnd + 1), style, output);
				index = gradientEnd + 1;
				return style;
			}

			Integer startColor = parseGradientColorSpec(header.substring(0, separatorIndex).trim());
			Integer endColor = parseGradientColorSpec(header.substring(separatorIndex + 1).trim());
			if (startColor == null || endColor == null) {
				appendRawLiteral(source.substring(start, gradientEnd + 1), style, output);
				index = gradientEnd + 1;
				return style;
			}

			GradientColorLayer gradientColorLayer = new GradientColorLayer(startColor, endColor);
			index = commaIndex + 1;
			List<InlineElement> gradientElements = new ArrayList<>();
			parseSequence(true, style.pushColorLayer(gradientColorLayer), gradientElements);
			if (index >= source.length() || source.charAt(index) != ']') {
				appendRawLiteral(source.substring(start, gradientEnd + 1), style, output);
				index = gradientEnd + 1;
				return style;
			}

			index++;
			output.addAll(gradientElements);
			return style;
		}

		private void appendText(String text, StyleState style, List<InlineElement> output) {
			output.add(new TextElement(text, style));
		}

		private void appendRawLiteral(String rawText, StyleState style, List<InlineElement> output) {
			for (int i = 0; i < rawText.length(); i++) {
				appendText(String.valueOf(rawText.charAt(i)), style, output);
			}
		}

		private @Nullable ColorDirective parseHexDirective(int start) {
			if (start + 8 > source.length() || source.charAt(start + 1) != '#') {
				return null;
			}

			String hex = source.substring(start + 2, start + 8);
			if (!hex.chars().allMatch(character -> Character.digit(character, 16) != -1)) {
				return null;
			}

			return new ColorDirective(new StaticColorLayer(Integer.parseInt(hex, 16)));
		}

		private @Nullable NamedDirective findNamedDirective(int start) {
			String tail = source.substring(start).toLowerCase();
			for (NamedDirective directive : NAMED_DIRECTIVES_BY_LENGTH) {
				if (tail.startsWith(directive.key())) {
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
}
