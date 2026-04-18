package me.Azz_9.flex_hud.client.customModules.modifiers;

import net.minecraft.text.Text;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Modifier<I, R> {

	private final String key;
	private final Class<I> inputType;
	private final Class<R> outputType;
	private final Function<String, @Nullable List<String>> parser;
	private final BiFunction<I, List<String>, R> modifierFunction;
	private final UiMetadata uiMetadata;

	public Modifier(String key,
	                Function<String, @Nullable List<String>> parser,
	                Class<I> inputType,
	                Class<R> outputType,
	                BiFunction<I, List<String>, R> modifierFunction,
	                UiMetadata uiMetadata) {
		this.key = Objects.requireNonNull(key, "key");
		this.parser = Objects.requireNonNull(parser, "parser");
		this.inputType = Objects.requireNonNull(inputType, "inputType");
		this.outputType = Objects.requireNonNull(outputType, "outputType");
		this.modifierFunction = Objects.requireNonNull(modifierFunction, "modifierFunction");
		this.uiMetadata = Objects.requireNonNull(uiMetadata, "uiMetadata");
	}

	public String key() {
		return key;
	}

	public @Nullable List<String> resolveArguments(String rawInput) {
		List<String> arguments = parser.apply(rawInput);
		return arguments != null ? List.copyOf(arguments) : null;
	}

	public Class<I> inputType() {
		return inputType;
	}

	public Class<R> outputType() {
		return outputType;
	}

	public R apply(I input, List<String> arguments) {
		return modifierFunction.apply(input, arguments);
	}

	public Object applyUnchecked(Object input, List<String> arguments) {
		if (input != null && !inputType.isInstance(input)) {
			throw new IllegalArgumentException("Modifier expected " + inputType.getSimpleName() + " but got " + input.getClass().getSimpleName());
		}

		return apply(inputType.cast(input), arguments);
	}

	public UiMetadata uiMetadata() {
		return uiMetadata;
	}

	public record UiMetadata(EditorKind editorKind,
	                         List<ParameterDefinition> parameters,
	                         Function<List<String>, String> rawFormatter,
	                         Function<List<String>, String> displayFormatter) {
		public UiMetadata {
			Objects.requireNonNull(editorKind, "editorKind");
			Objects.requireNonNull(parameters, "parameters");
			Objects.requireNonNull(rawFormatter, "rawFormatter");
			Objects.requireNonNull(displayFormatter, "displayFormatter");
			parameters = List.copyOf(parameters);
		}

		public Text getName(String modifierKey) {
			return Text.translatable("flex_hud.custom_modules.modifier.name." + modifierKey);
		}

		public Text getDescription(String modifierKey) {
			return Text.translatable("flex_hud.custom_modules.modifier.description." + modifierKey);
		}
	}

	public record ParameterDefinition(String key, ParameterKind kind) {
		public ParameterDefinition {
			Objects.requireNonNull(key, "key");
			Objects.requireNonNull(kind, "kind");
		}

		public Text getName(String modifierKey) {
			return Text.translatable("flex_hud.custom_modules.modifier.parameter." + key);
		}
	}

	public enum ParameterKind {
		INTEGER,
		DECIMAL,
		TEXT,
		CHARACTER,
		CONDITIONAL_BRANCHES
	}

	public enum EditorKind {
		NONE,
		FIXED_FIELDS,
		CONDITIONAL_BRANCHES
	}
}
