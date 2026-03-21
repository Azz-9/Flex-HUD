package me.Azz_9.flex_hud.client.customModules.token;

record TextToken(String text) implements Token {

	@Override
	public String getString() {
		return text;
	}
}