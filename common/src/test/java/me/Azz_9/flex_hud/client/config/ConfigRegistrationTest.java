package me.Azz_9.flex_hud.client.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.client.config.option.AbstractConfigObject;
import me.Azz_9.flex_hud.client.modules.Modules;

public class ConfigRegistrationTest {

	@Test
	void allConfigObjectsAreRegistered() {
		Modules.getInstance(false);
		for (Configurable module : Modules.getConfigurableModules()) {

			for (Field field : getConfigFields(module.getClass())) {
				assertTrue(
						ConfigRegistry.isRegistered(module.getID(), field.getName()),
						field.getName() + " is not registered in ConfigRegistry in " + module.getID() + " module"
				);
			}
		}
	}

	public static List<Field> getConfigFields(Class<?> clazz) {
		List<Field> result = new ArrayList<>();

		for (Field field : clazz.getDeclaredFields()) {

			if (AbstractConfigObject.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);
				result.add(field);
			}
		}

		return result;
	}
}
