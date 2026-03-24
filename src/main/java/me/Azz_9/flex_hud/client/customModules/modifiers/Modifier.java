package me.Azz_9.flex_hud.client.customModules.modifiers;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class Modifier<I, R> {

	private final Pattern regex;
	private final Class<I> inputType;
	private final Class<R> outputType;
	private final BiFunction<I, String, R> modifierFunction;

	public Modifier(Pattern regex, Class<I> inputType, Class<R> outputType, BiFunction<I, String, R> modifierFunction) {
		this.regex = Objects.requireNonNull(regex, "regex");
		this.inputType = Objects.requireNonNull(inputType, "inputType");
		this.outputType = Objects.requireNonNull(outputType, "outputType");
		this.modifierFunction = Objects.requireNonNull(modifierFunction, "modifierFunction");
	}

	public boolean matches(String input) {
		return regex.matcher(input).matches();
	}

	public Class<I> inputType() {
		return inputType;
	}

	public Class<R> outputType() {
		return outputType;
	}

	public R apply(I input, String key) {
		return modifierFunction.apply(input, key);
	}

	public Object applyUnchecked(Object input, String key) {
		if (input != null && !inputType.isInstance(input)) {
			throw new IllegalArgumentException("Modifier expected " + inputType.getSimpleName() + " but got " + input.getClass().getSimpleName());
		}

		return apply(inputType.cast(input), key);
	}
}
