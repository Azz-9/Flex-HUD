package me.Azz_9.better_hud.client.configurableMods.mods.hud;

import me.Azz_9.better_hud.client.configurableMods.mods.Translatable;

public enum DisplayMode implements Translatable {
	HORIZONTAL("better_hud.enum.display_mode.horizontal"),
	VERTICAL("better_hud.enum.display_mode.vertical");

	private final String translationKey;

	DisplayMode(String translationKey) {
		this.translationKey = translationKey;
	}

	public String getTranslationKey() {
		return translationKey;
	}
}
