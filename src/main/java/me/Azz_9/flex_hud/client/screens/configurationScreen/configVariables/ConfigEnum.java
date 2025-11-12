package me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.Azz_9.flex_hud.client.Flex_hudClient;

import java.util.Locale;

public class ConfigEnum<E extends Enum<E>> extends AbstractConfigObject<E> {

	private final Class<E> enumClass;

	public ConfigEnum(Class<E> enumClass, E defaultValue, String configTextTranslationKey) {
		super(defaultValue, configTextTranslationKey);
		this.enumClass = enumClass;
	}

	public ConfigEnum(Class<E> enumClass, E defaultValue) {
		super(defaultValue);
		this.enumClass = enumClass;
	}

	@Override
	protected E parseValue(JsonElement element) {
		if (element == null || element.isJsonNull()) return getDefaultValue();

		try {
			String name = element.getAsString().toUpperCase(Locale.ROOT);
			return Enum.valueOf(enumClass, name);
		} catch (Exception e) {
			Flex_hudClient.LOGGER.warn("Unknown enum value '{}' for {}, using default.", element, enumClass.getSimpleName());
			return getDefaultValue();
		}
	}

	@Override
	public JsonElement toJsonValue() {
		return new JsonPrimitive(getValue().name());
	}
}
