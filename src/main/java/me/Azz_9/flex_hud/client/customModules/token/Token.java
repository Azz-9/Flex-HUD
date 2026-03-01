package me.Azz_9.flex_hud.client.customModules.token;

import org.jspecify.annotations.NonNull;

sealed public interface Token permits TextToken, VariableToken {

	@NonNull
	String getString();
}