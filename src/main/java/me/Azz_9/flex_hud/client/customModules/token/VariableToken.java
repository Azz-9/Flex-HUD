package me.Azz_9.flex_hud.client.customModules.token;

import java.util.List;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.customModules.Variable;
import me.Azz_9.flex_hud.client.customModules.modifiers.Modifiers;

record VariableToken<T>(Variable<T> variable, List<Modifiers.ResolvedModifier<?, ?>> modifiers,
						Function<T, String> formatter) implements Token {

	VariableToken(Variable<T> variable, List<Modifiers.ResolvedModifier<?, ?>> modifiers) {
		this(variable, modifiers, Modifiers.formatterFromModifiers(modifiers));
	}

	@Override
	public String getString() {
		return formatter.apply(variable.getValue());
	}
}
