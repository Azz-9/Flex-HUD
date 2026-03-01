package me.Azz_9.flex_hud.client.customModules;

record TextToken(String text) implements Token {
	
	@Override
	public String getString() {
		return text;
	}
}