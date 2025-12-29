package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

public class ConfigInteger extends AbstractConfigObject<Integer> {
	private final int MIN;
	private final int MAX;

	public ConfigInteger(Integer defaultValue, String configTextTranslationKey, Integer min, Integer max) {
		super(defaultValue, configTextTranslationKey);
		this.MIN = (min == null ? 0 : min);
		this.MAX = (max == null ? Integer.MAX_VALUE : max);
	}

	public ConfigInteger(Integer defaultValue, String configTextTranslationKey) {
		this(defaultValue, configTextTranslationKey, 0, Integer.MAX_VALUE);
	}

	public ConfigInteger(Integer defaultValue, Integer min, Integer max) {
		this(defaultValue, null, min, max);
	}

	public ConfigInteger(Integer defaultValue) {
		this(defaultValue, null);
	}

	@Override
	protected Integer parseValue(JsonElement element) {
		if (element == null || element.isJsonNull()) return getDefaultValue();
		return element.getAsInt();
	}

	@Override
	public JsonElement toJsonValue() {
		if (getValue() == null) return JsonNull.INSTANCE;
		return new JsonPrimitive(getValue());
	}

	public int getMin() {
		return MIN;
	}

	public int getMax() {
		return MAX;
	}
}
