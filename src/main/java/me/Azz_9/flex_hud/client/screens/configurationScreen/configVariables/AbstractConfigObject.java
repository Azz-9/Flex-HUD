package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

import com.google.gson.JsonElement;

public abstract class AbstractConfigObject<T> {
	private T value;
	private T defaultValue;
	private String configTextTranslationKey;

	public AbstractConfigObject(T defaultValue, String configTextTranslationKey) {
		this.defaultValue = defaultValue;
		this.configTextTranslationKey = configTextTranslationKey;
		this.value = defaultValue;
	}

	public AbstractConfigObject(T defaultValue) {
		this(defaultValue, null);
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getConfigTextTranslationKey() {
		return configTextTranslationKey;
	}

	public void setConfigTextTranslationKey(String configTextTranslationKey) {
		this.configTextTranslationKey = configTextTranslationKey;
	}

	public void setToDefault() {
		this.value = defaultValue;
	}

	public void applyFromJsonElement(JsonElement element) {
		if (element == null || element.isJsonNull()) return;
		this.value = parseValue(element);
	}

	protected abstract T parseValue(JsonElement element);

	public abstract JsonElement toJsonValue();
}
