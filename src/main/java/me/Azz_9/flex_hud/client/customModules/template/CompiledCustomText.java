package me.Azz_9.flex_hud.client.customModules.template;

import static java.util.Objects.requireNonNull;
import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.Variables;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;
import me.Azz_9.flex_hud.client.customModules.text.CustomCondition;
import me.Azz_9.flex_hud.client.customModules.text.CustomTextParser;
import me.Azz_9.flex_hud.client.tickables.ChromaColorTickable;

public final class CompiledCustomText {

	private static final int DEFAULT_STYLED_TEXT_COLOR = 0xffffff;

	private final String source;
	private final SequenceNode root;
	private final List<CompiledVariable> variables;
	private final List<CompiledCondition> conditions;
	private final boolean hasExplicitColors;
	private final boolean hasDynamicColors;

	private @Nullable RenderData cachedRenderData;

	private CompiledCustomText(String source,
	                           SequenceNode root,
	                           List<CompiledVariable> variables,
	                           List<CompiledCondition> conditions,
	                           boolean hasExplicitColors,
	                           boolean hasDynamicColors) {
		this.source = source;
		this.root = root;
		this.variables = List.copyOf(variables);
		this.conditions = List.copyOf(conditions);
		this.hasExplicitColors = hasExplicitColors;
		this.hasDynamicColors = hasDynamicColors;
	}

	public static CompiledCustomText compile(String source) {
		return compile(source, Variables::get);
	}

	public static CompiledCustomText compile(String source, Function<String, @Nullable Variable<?>> variableResolver) {
		CustomTextParser.ParsedDocument parsedDocument = CustomTextParser.parse(source, variableResolver);
		BuildContext buildContext = new BuildContext();
		SequenceNode root = compileSequence(parsedDocument.root(), buildContext);
		return new CompiledCustomText(source, root, buildContext.variables, buildContext.conditions, parsedDocument.hasExplicitColors(), parsedDocument.hasDynamicColors());
	}

	private static SequenceNode compileSequence(CustomTextParser.SequenceNode sequence, BuildContext buildContext) {
		List<Node> nodes = new ArrayList<>(sequence.children().size());
		for (CustomTextParser.Node child : sequence.children()) {
			nodes.add(compileNode(child, buildContext));
		}
		return new SequenceNode(List.copyOf(nodes));
	}

	private static Node compileNode(CustomTextParser.Node node, BuildContext buildContext) {
		if (node instanceof CustomTextParser.LiteralNode literalNode) {
			return new LiteralNode(literalNode.text());
		}

		if (node instanceof CustomTextParser.VariableNode variableNode) {
			CompiledVariable compiledVariable = new CompiledVariable(variableNode.rawPlaceholder(), variableNode.variable(), variableNode.modifiers());
			compiledVariable.refreshIfNeeded();
			buildContext.variables.add(compiledVariable);
			return new VariableNode(compiledVariable);
		}

		if (node instanceof CustomTextParser.ConditionNode conditionNode) {
			CompiledCondition compiledCondition = new CompiledCondition(conditionNode.condition());
			compiledCondition.refreshIfNeeded();
			buildContext.conditions.add(compiledCondition);
			return new ConditionNode(compiledCondition, compileSequence(conditionNode.content(), buildContext));
		}

		if (node instanceof CustomTextParser.DirectiveNode directiveNode) {
			return new DirectiveNode(compileDirective(directiveNode.directive()));
		}

		CustomTextParser.GradientNode gradientNode = (CustomTextParser.GradientNode) node;
		return new GradientNode(gradientNode.startColor(), gradientNode.endColor(), compileSequence(gradientNode.content(), buildContext));
	}

	private static StyleDirective compileDirective(CustomTextParser.Directive directive) {
		if (directive instanceof CustomTextParser.ToggleDirective toggleDirective) {
			return switch (toggleDirective) {
				case BOLD -> ToggleStyleDirective.BOLD;
				case ITALIC -> ToggleStyleDirective.ITALIC;
				case UNDERLINE -> ToggleStyleDirective.UNDERLINE;
				case STRIKETHROUGH -> ToggleStyleDirective.STRIKETHROUGH;
				case OBFUSCATED -> ToggleStyleDirective.OBFUSCATED;
			};
		}

		if (directive == CustomTextParser.ResetDirective.INSTANCE) {
			return ResetDirective.INSTANCE;
		}

		CustomTextParser.ColorDirective colorDirective = (CustomTextParser.ColorDirective) directive;
		return switch (colorDirective.kind()) {
			case STATIC -> new ColorDirective(new StaticColorSource(requireNonNull(colorDirective.rgb(), "rgb")));
			case CHROMA -> new ColorDirective(DynamicColorSource.INSTANCE);
		};
	}

	public RenderData getRenderData() {
		boolean shouldRebuild = hasDynamicColors || cachedRenderData == null;
		for (CompiledVariable variable : variables) {
			shouldRebuild |= variable.refreshIfNeeded();
		}
		for (CompiledCondition condition : conditions) {
			shouldRebuild |= condition.refreshIfNeeded();
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
		for (CompiledCondition condition : conditions) {
			condition.refreshIfNeeded();
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
			if (glyph.style().currentColorSource() == GradientPlaceholderColorSource.INSTANCE) {
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
			return new StyledGlyph(text, style.replaceCurrentColorSource(colorSource));
		}
	}

	private sealed interface Node permits SequenceNode, LiteralNode, VariableNode, ConditionNode, DirectiveNode, GradientNode {
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

	private record ConditionNode(CompiledCondition condition, SequenceNode content) implements Node {
		@Override
		public StyleState render(StyleState style, List<StyledGlyph> output, WidthMeasurer widthMeasurer) {
			if (condition.test()) {
				content.render(style, output, widthMeasurer);
			}
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
			content.render(style.pushColorSource(GradientPlaceholderColorSource.INSTANCE), gradientGlyphs, widthMeasurer);
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
	                          List<ColorSource> colorSources) {

		private static final StyleState EMPTY = new StyleState(
				false, false, false, false, false, List.of()
		);

		private StyleState withBold(boolean newValue) {
			return new StyleState(newValue, italic, underline, strikethrough, obfuscated, colorSources);
		}

		private StyleState withItalic(boolean newValue) {
			return new StyleState(bold, newValue, underline, strikethrough, obfuscated, colorSources);
		}

		private StyleState withUnderline(boolean newValue) {
			return new StyleState(bold, italic, newValue, strikethrough, obfuscated, colorSources);
		}

		private StyleState withStrikethrough(boolean newValue) {
			return new StyleState(bold, italic, underline, newValue, obfuscated, colorSources);
		}

		private StyleState withObfuscated(boolean newValue) {
			return new StyleState(bold, italic, underline, strikethrough, newValue, colorSources);
		}

		private StyleState pushColorSource(ColorSource newColorSource) {
			List<ColorSource> newColorSources = new ArrayList<>(colorSources);
			newColorSources.add(newColorSource);
			return new StyleState(bold, italic, underline, strikethrough, obfuscated, List.copyOf(newColorSources));
		}

		private StyleState toggleColorSource(ColorSource toggledColorSource) {
			if (!colorSources.isEmpty() && colorSources.get(colorSources.size() - 1).equals(toggledColorSource)) {
				return new StyleState(
						bold,
						italic,
						underline,
						strikethrough,
						obfuscated,
						List.copyOf(colorSources.subList(0, colorSources.size() - 1))
				);
			}

			return pushColorSource(toggledColorSource);
		}

		private StyleState replaceCurrentColorSource(ColorSource newColorSource) {
			if (colorSources.isEmpty()) {
				return pushColorSource(newColorSource);
			}

			List<ColorSource> newColorSources = new ArrayList<>(colorSources);
			newColorSources.set(newColorSources.size() - 1, newColorSource);
			return new StyleState(bold, italic, underline, strikethrough, obfuscated, List.copyOf(newColorSources));
		}

		private StyleState withoutColorSource() {
			return new StyleState(bold, italic, underline, strikethrough, obfuscated, List.of());
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

			ColorSource colorSource = currentColorSource();
			Integer rgb = colorSource != null
					? colorSource.resolveRgb(forceDefaultColor)
					: (forceDefaultColor ? DEFAULT_STYLED_TEXT_COLOR : null);
			if (rgb != null) {
				style = style.withColor(TextColor.fromRgb(rgb));
			}

			return style;
		}

		private Style toMeasurementStyle() {
			return withoutColorSource().toMinecraftStyle(false);
		}

		private @Nullable ColorSource currentColorSource() {
			return colorSources.isEmpty() ? null : colorSources.get(colorSources.size() - 1);
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
			return style.toggleColorSource(colorSource);
		}
	}

	private enum ResetDirective implements StyleDirective {
		INSTANCE;

		@Override
		public StyleState apply(StyleState style) {
			return style.reset();
		}
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

	private static final class CompiledCondition {
		private final CustomCondition.Condition condition;
		private final List<Variable<?>> dependencies;
		private List<Long> lastSeenVersions = List.of();
		private boolean lastResult;

		private CompiledCondition(CustomCondition.Condition condition) {
			this.condition = condition;
			this.dependencies = condition.dependencies();
		}

		private boolean refreshIfNeeded() {
			List<Long> versions = dependencies.stream().map(Variable::getVersion).toList();
			boolean result = condition.test();
			boolean changed = !versions.equals(lastSeenVersions) || result != lastResult;
			lastSeenVersions = versions;
			lastResult = result;
			return changed;
		}

		private boolean test() {
			return lastResult;
		}
	}

	private static final class BuildContext {
		private final List<CompiledVariable> variables = new ArrayList<>();
		private final List<CompiledCondition> conditions = new ArrayList<>();
	}

	@FunctionalInterface
	private interface WidthMeasurer {
		int measure(String text, Style style);
	}
}
