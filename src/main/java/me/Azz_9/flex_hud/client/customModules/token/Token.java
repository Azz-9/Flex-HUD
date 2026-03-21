package me.Azz_9.flex_hud.client.customModules.token;

sealed public interface Token permits TextToken, VariableToken {

	String getString();
}