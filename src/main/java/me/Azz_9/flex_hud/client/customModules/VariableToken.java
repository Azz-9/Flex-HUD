package me.Azz_9.flex_hud.client.customModules;

record VariableToken(Variable<?> variable) implements Token {

	@Override
	public String getString() {
		return variable.resolve();
	}
}