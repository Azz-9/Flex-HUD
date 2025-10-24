package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;

public enum DisplayMode implements Translatable {
	HORIZONTAL("flex_hud.enum.display_mode.horizontal"),
	VERTICAL("flex_hud.enum.display_mode.vertical");

	private final String translationKey;

	DisplayMode(String translationKey) {
		this.translationKey = translationKey;
	}

	public String getTranslationKey() {
		return translationKey;
	}
}
