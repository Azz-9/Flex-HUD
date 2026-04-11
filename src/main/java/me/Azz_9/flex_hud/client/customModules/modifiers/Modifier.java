package me.Azz_9.flex_hud.client.customModules.modifiers;

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

	public Modifier(String key,
	                Function<String, @Nullable List<String>> parser,
	                Class<I> inputType,
	                Class<R> outputType,
	                BiFunction<I, List<String>, R> modifierFunction) {
		this.key = Objects.requireNonNull(key, "key");
		this.parser = Objects.requireNonNull(parser, "parser");
		this.inputType = Objects.requireNonNull(inputType, "inputType");
		this.outputType = Objects.requireNonNull(outputType, "outputType");
		this.modifierFunction = Objects.requireNonNull(modifierFunction, "modifierFunction");
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
}
