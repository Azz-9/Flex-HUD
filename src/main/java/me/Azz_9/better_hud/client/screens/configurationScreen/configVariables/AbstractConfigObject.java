package me.Azz_9.better_hud.client.screens.configurationScreen.configVariables;

import net.minecraft.text.Text;

public abstract class AbstractConfigObject<T> {
	private T value;
	private final T DEFAULT_VALUE;
	private Text configText;

	public AbstractConfigObject(T defaultValue, Text configText) {
		this.DEFAULT_VALUE = defaultValue;
		this.configText = configText;
	}

	public T getDefaultValue() {
		return DEFAULT_VALUE;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Text getConfigText() {
		return configText;
	}

	public void setToDefault() {
		this.value = DEFAULT_VALUE;
	}
}
