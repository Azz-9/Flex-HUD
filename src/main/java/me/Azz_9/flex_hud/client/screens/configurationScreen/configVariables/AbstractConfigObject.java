package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class AbstractConfigObject<T> {
	@NotNull
	private T value;
	@NotNull
	private T defaultValue;
	@Nullable
	private String configTextTranslationKey;
	@Nullable
	private Consumer<T> onChange = null;

	public AbstractConfigObject(@NotNull final T defaultValue, @Nullable final String configTextTranslationKey) {
		this.defaultValue = defaultValue;
		this.configTextTranslationKey = configTextTranslationKey;
		this.value = defaultValue;
	}

	public AbstractConfigObject(@NotNull final T defaultValue) {
		this(defaultValue, null);
	}

	public @NotNull T getDefaultValue() {
		return defaultValue;
	}

	public @NotNull T getValue() {
		return value;
	}

	public void setValue(@NotNull final T value) {
		if (onChange != null && !this.value.equals(value)) onChange.accept(value);
		this.value = value;
	}

	public void setDefaultValue(@NotNull final T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public @Nullable String getConfigTextTranslationKey() {
		return configTextTranslationKey;
	}

	public void setConfigTextTranslationKey(@Nullable String configTextTranslationKey) {
		this.configTextTranslationKey = configTextTranslationKey;
	}

	public void setToDefault() {
		setValue(defaultValue);
	}

	public void setOnChange(@Nullable Consumer<T> onChange) {
		this.onChange = onChange;
	}

	public void applyFromJsonElement(JsonElement element) {
		if (element == null || element.isJsonNull()) return;
		setValue(parseValue(element));
	}

	protected abstract T parseValue(JsonElement element);

	public abstract JsonElement toJsonValue();
}
