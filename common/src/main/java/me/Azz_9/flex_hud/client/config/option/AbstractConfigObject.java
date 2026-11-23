package me.Azz_9.flex_hud.client.config.option;

import com.demonwav.mcdev.annotations.Translatable;
import com.google.gson.JsonElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class AbstractConfigObject<T> {
	private @NotNull T value;
	private @NotNull T defaultValue;
	private @Nullable String configTextTranslationKey;
	private @Nullable Consumer<T> onChange = null;

	public AbstractConfigObject(final @NotNull T defaultValue, final @Nullable @Translatable String configTextTranslationKey) {
		this.defaultValue = defaultValue;
		this.configTextTranslationKey = configTextTranslationKey;
		this.value = defaultValue;
	}

	public AbstractConfigObject(final @NotNull T defaultValue) {
		this(defaultValue, null);
	}

	public @NotNull T getDefaultValue() {
		return defaultValue;
	}

	public @NotNull T getValue() {
		return value;
	}

	public void setValue(final @NotNull T value) {
		if (onChange != null && !this.value.equals(value)) onChange.accept(value);
		this.value = value;
	}

	public void setDefaultValue(final @NotNull T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public @Nullable String getConfigTextTranslationKey() {
		return configTextTranslationKey;
	}

	public void setConfigTextTranslationKey(final @Nullable @Translatable String configTextTranslationKey) {
		this.configTextTranslationKey = configTextTranslationKey;
	}

	public void setToDefault() {
		setValue(defaultValue);
	}

	public void setOnChange(final @Nullable Consumer<T> onChange) {
		this.onChange = onChange;
	}

	public void applyFromJsonElement(final @Nullable JsonElement element) {
		if (element == null || element.isJsonNull()) return;
		setValue(parseValue(element));
	}

	protected abstract T parseValue(JsonElement element);

	public abstract JsonElement toJsonValue();
}
