package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ConfigString extends AbstractConfigObject<String> {

	public ConfigString(String defaultValue, String configTextTranslationKey) {
		super(defaultValue, configTextTranslationKey);
	}

	public ConfigString(String defaultValue) {
		super(defaultValue);
	}

	@Override
	protected String parseValue(JsonElement element) {
		if (element == null || element.isJsonNull()) return getDefaultValue();
		return element.getAsString();
	}

	@Override
	public JsonElement toJsonValue() {
		return new JsonPrimitive(getValue());
	}
}
