package me.Azz_9.flex_hud.client.modules.customModules.token;

import org.jetbrains.annotations.NotNull;

sealed public interface Token permits TextToken, VariableToken {

	@NotNull
	String getString();
}