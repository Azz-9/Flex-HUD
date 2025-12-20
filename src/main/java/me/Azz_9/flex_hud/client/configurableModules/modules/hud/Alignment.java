package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;

public enum Alignment implements Translatable {
	LEFT("flex_hud.enum.alignment.left"),
	CENTER("flex_hud.enum.alignment.center"),
	RIGHT("flex_hud.enum.alignment.right"),
	AUTO("flex_hud.enum.alignment.auto");

	private final String translationKey;

	Alignment(String translationKey) {
		this.translationKey = translationKey;
	}

	@Override
	public String getTranslationKey() {
		return translationKey;
	}
}
