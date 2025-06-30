package me.Azz_9.better_hud.client.screens.configurationScreen.configVariables;

public abstract class AbstractConfigObject<T> {
	private T value;
	private T defaultValue;
	private String configTextTranslationKey;

	public AbstractConfigObject(T defaultValue, String configTextTranslationKey) {
		this.defaultValue = defaultValue;
		this.configTextTranslationKey = configTextTranslationKey;
		this.value = defaultValue;
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
}
