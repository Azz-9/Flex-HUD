package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

public class ConfigBoolean extends AbstractConfigObject<Boolean> {

	public ConfigBoolean(Boolean defaultValue, String configTextTranslationKey) {
		super(defaultValue, configTextTranslationKey);
	}

	public ConfigBoolean(Boolean defaultValue) {
		super(defaultValue);
	}

	@Override
	protected Boolean parseValue(JsonElement element) {
		if (element == null || element.isJsonNull()) return getDefaultValue();
		return element.getAsBoolean();
	}

	@Override
	public JsonElement toJsonValue() {
		if (getValue() == null) return JsonNull.INSTANCE;
		return new JsonPrimitive(getValue());
	}
}
