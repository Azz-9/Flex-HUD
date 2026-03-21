package me.Azz_9.flex_hud.client.customModules.token;

import me.Azz_9.flex_hud.client.customModules.Variable;

record VariableToken(Variable<?> variable) implements Token {

	@Override
	public String getString() {
		return variable.resolve();
	}
}