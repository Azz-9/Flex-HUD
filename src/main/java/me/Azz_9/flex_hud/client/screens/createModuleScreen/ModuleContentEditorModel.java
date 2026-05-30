package me.Azz_9.flex_hud.client.screens.createModuleScreen;

import static java.util.Objects.requireNonNull;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.Variables;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;
import me.Azz_9.flex_hud.client.customModules.text.CustomCondition;
import me.Azz_9.flex_hud.client.customModules.text.CustomTextParser;

public final class ModuleContentEditorModel {

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
		return fromSequence(CustomTextParser.parse(rawText, Variables::get).root(), initialStyle);
	}

	private static ModuleContentEditorModel fromSequence(CustomTextParser.SequenceNode sequence, StyleState initialStyle) {
		List<InlineElement> elements = new ArrayList<>();
		appendSequence(sequence, initialStyle, elements);
		return new ModuleContentEditorModel(elements);
	}

	private static StyleState appendSequence(CustomTextParser.SequenceNode sequence, StyleState style, List<InlineElement> output) {
		StyleState current = style;
		for (CustomTextParser.Node node : sequence.children()) {
			current = appendNode(node, current, output);
		}
		return current;
	}

	private static StyleState appendNode(CustomTextParser.Node node, StyleState style, List<InlineElement> output) {
		if (node instanceof CustomTextParser.LiteralNode(String text)) {
			appendLiteral(text, style, output);
			return style;
		}

		if (node instanceof CustomTextParser.VariableNode variableNode) {
			output.add(new VariableElement(variableNode.key(), variableNode.variable(), variableNode.modifiers(), style));
			return style;
		}

		if (node instanceof CustomTextParser.ConditionNode conditionNode) {
			output.add(new ConditionElement(conditionNode.condition(), fromSequence(conditionNode.content(), StyleState.EMPTY), style));
			return style;
		}

		if (node instanceof CustomTextParser.DirectiveNode(CustomTextParser.Directive directive)) {
			return applyDirective(directive, style);
		}

		CustomTextParser.GradientNode gradientNode = (CustomTextParser.GradientNode) node;
		appendSequence(gradientNode.content(), style.pushColorLayer(new GradientColorLayer(gradientNode.startColor(), gradientNode.endColor())), output);
		return style;
	}

	private static void appendLiteral(String text, StyleState style, List<InlineElement> output) {
		text.codePoints()
				.mapToObj(Character::toChars)
				.map(String::new)
				.map(character -> new TextElement(character, style))
				.forEach(output::add);
	}

	private static StyleState applyDirective(CustomTextParser.Directive directive, StyleState style) {
		if (directive instanceof CustomTextParser.ToggleDirective toggleDirective) {
			return switch (toggleDirective) {
				case BOLD -> style.withBold(!style.bold());
				case ITALIC -> style.withItalic(!style.italic());
				case UNDERLINE -> style.withUnderline(!style.underline());
				case STRIKETHROUGH -> style.withStrikethrough(!style.strikethrough());
				case OBFUSCATED -> style.withObfuscated(!style.obfuscated());
			};
		}

		if (directive == CustomTextParser.ResetDirective.INSTANCE) {
			return style.reset();
		}

		CustomTextParser.ColorDirective colorDirective = (CustomTextParser.ColorDirective) directive;
		return switch (colorDirective.kind()) {
			case STATIC -> style.toggleColorLayer(new StaticColorLayer(requireNonNull(colorDirective.rgb(), "rgb")));
			case CHROMA -> style.toggleColorLayer(ChromaColorLayer.INSTANCE);
		};
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

	public void insertCondition(int index, CustomCondition.Condition condition, ModuleContentEditorModel content, StyleState style) {
		elements.add(Math.clamp(index, 0, elements.size()), new ConditionElement(condition, content.copy(), style));
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

	public void updateCondition(int elementIndex, CustomCondition.Condition condition, ModuleContentEditorModel content) {
		if (0 <= elementIndex && elementIndex < elements.size() && elements.get(elementIndex) instanceof ConditionElement conditionElement) {
			conditionElement.setCondition(condition);
			conditionElement.setContent(content);
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

	public String visibleText() {
		StringBuilder builder = new StringBuilder();
		for (InlineElement element : elements) {
			builder.append(element.visibleText());
		}
		return builder.toString();
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
			if (node instanceof LeafNode(List<InlineElement> elements1)) {
				current = serializeLeaf(elements1, current, builder);
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
			if (node instanceof LeafNode(List<InlineElement> elements1)) {
				current = serializeLeaf(elements1, current, builder);
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

		if (element instanceof ConditionElement conditionElement) {
			return "{" + CustomCondition.PREFIX + conditionElement.condition().format() + "|" + conditionElement.content().serialize() + "}";
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
		if (colorLayer instanceof StaticColorLayer(int rgb)) {
			return "&#" + String.format("%06x", rgb);
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

	public sealed static abstract class InlineElement permits TextElement, VariableElement, ConditionElement {
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

	public static final class ConditionElement extends InlineElement {
		private CustomCondition.Condition condition;
		private ModuleContentEditorModel content;

		public ConditionElement(CustomCondition.Condition condition, ModuleContentEditorModel content, StyleState style) {
			super(style);
			this.condition = requireNonNull(condition, "condition");
			this.content = requireNonNull(content, "content").copy();
		}

		public CustomCondition.Condition condition() {
			return condition;
		}

		public ModuleContentEditorModel content() {
			return content.copy();
		}

		public void setCondition(CustomCondition.Condition condition) {
			this.condition = requireNonNull(condition, "condition");
		}

		public void setContent(ModuleContentEditorModel content) {
			this.content = requireNonNull(content, "content").copy();
		}

		@Override
		public InlineElement copy() {
			return new ConditionElement(condition, content, style());
		}

		@Override
		public String visibleText() {
			return "if " + condition.displayText() + " -> " + content.visibleText();
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
				if (topLayer instanceof StaticColorLayer(int rgb)) {
					return ColorSummary.staticColor(rgb);
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
}
