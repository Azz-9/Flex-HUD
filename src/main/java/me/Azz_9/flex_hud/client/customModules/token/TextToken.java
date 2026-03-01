package me.Azz_9.flex_hud.client.customModules.token;

import org.jetbrains.annotations.NotNull;

record TextToken(String text) implements Token {

	@Override
	public @NotNull String getString() {
		return text;
	}
}