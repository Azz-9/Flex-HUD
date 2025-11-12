package me.Azz_9.flex_hud.client.configurableModules;

import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.AbstractConfigObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigRegistry {
	private static final Map<String, Map<String, AbstractConfigObject<?>>> MODULES = new HashMap<>();

	public static void register(String moduleName, String key, AbstractConfigObject<?> configObject) {
		MODULES.computeIfAbsent(moduleName, k -> new HashMap<>()).put(key, configObject);
	}

	public static Map<String, AbstractConfigObject<?>> getModule(String moduleName) {
		return MODULES.get(moduleName);
	}

	public static Set<String> getModuleNames() {
		return MODULES.keySet();
	}
}
