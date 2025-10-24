package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;

public class ConfigEnum<E extends Enum<E> & Translatable> extends AbstractConfigObject<E> {

	public ConfigEnum(E defaultValue, String configTextTranslationKey) {
		super(defaultValue, configTextTranslationKey);
	}
}
