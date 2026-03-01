package me.Azz_9.flex_hud.client.customModules;

import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;

public class CustomModuleRegistry {

	private static final Set<String> registered = new HashSet<>();

	public static void register(CustomModule module) throws IllegalStateException {
		if (registered.contains(module.getID())) {
			throw new IllegalStateException("CustomModule already registered: " + module.getID());
		}
		ModulesHelper.addCustomModule(module);
		registered.add(module.getID());
	}

	public static void unregister(CustomModule module) {
		ModulesHelper.removeCustomModule(module);
		registered.remove(module.getID());
	}

	public static boolean isRegistered(@NonNull String id) {
		return registered.contains(id);
	}
}
