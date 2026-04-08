package me.Azz_9.flex_hud.client.customModules.modifiers;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Modifier<I, R> {

	private final Pattern regex;
	private final Class<I> inputType;
	private final Class<R> outputType;
	private final BiFunction<I, Matcher, R> modifierFunction;

	public Modifier(Pattern regex, Class<I> inputType, Class<R> outputType, BiFunction<I, Matcher, R> modifierFunction) {
		this.regex = Objects.requireNonNull(regex, "regex");
		this.inputType = Objects.requireNonNull(inputType, "inputType");
		this.outputType = Objects.requireNonNull(outputType, "outputType");
		this.modifierFunction = Objects.requireNonNull(modifierFunction, "modifierFunction");
	}

	public Pattern getRegex() {
		return regex;
	}

	public Class<I> inputType() {
		return inputType;
	}

	public Class<R> outputType() {
		return outputType;
	}

	public R apply(I input, Matcher matcher) {
		return modifierFunction.apply(input, matcher);
	}

	public Object applyUnchecked(Object input, Matcher matcher) {
		if (input != null && !inputType.isInstance(input)) {
			throw new IllegalArgumentException("Modifier expected " + inputType.getSimpleName() + " but got " + input.getClass().getSimpleName());
		}

		return apply(inputType.cast(input), matcher);
	}
}
