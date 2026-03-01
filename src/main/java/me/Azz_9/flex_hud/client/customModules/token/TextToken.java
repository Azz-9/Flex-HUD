package me.Azz_9.flex_hud.client.customModules.token;

import org.jspecify.annotations.NonNull;

record TextToken(String text) implements Token {

	@Override
	public @NonNull String getString() {
		return text;
	}
}