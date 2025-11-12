package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

import com.google.gson.JsonElement;

public class ConfigFloat extends AbstractConfigObject<Float> {

	public ConfigFloat(Float defaultValue, String configTextTranslationKey) {
		super(defaultValue, configTextTranslationKey);
	}

	public ConfigFloat(Float defaultValue) {
		super(defaultValue);
	}

	@Override
	protected Float parseValue(JsonElement element) {
		return element.getAsFloat();
	}
}
