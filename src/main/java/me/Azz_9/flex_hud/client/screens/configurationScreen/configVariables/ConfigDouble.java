package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

import com.google.gson.JsonElement;

public class ConfigDouble extends AbstractConfigObject<Double> {

	public ConfigDouble(Double defaultValue, String configTextTranslationKey) {
		super(defaultValue, configTextTranslationKey);
	}

	public ConfigDouble(Double defaultValue) {
		super(defaultValue);
	}

	@Override
	protected Double parseValue(JsonElement element) {
		return element.getAsDouble();
	}
}
