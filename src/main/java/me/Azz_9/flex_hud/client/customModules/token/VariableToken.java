package me.Azz_9.flex_hud.client.customModules.token;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.customModules.Variable;

record VariableToken<T>(Variable<T> variable,
						List<String> modifiers,
						Function<T, String> formatter) implements Token {

	public VariableToken(Variable<T> variable, List<String> modifiers) {
		this(variable, modifiers, Objects::toString);
	}

	@Override
	public String getString() {
		return formatter.apply(variable.getValue());
	}
}