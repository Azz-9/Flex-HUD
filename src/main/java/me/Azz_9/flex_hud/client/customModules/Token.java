package me.Azz_9.flex_hud.client.customModules;

sealed interface Token permits TextToken, VariableToken {
	
	String getString();
}