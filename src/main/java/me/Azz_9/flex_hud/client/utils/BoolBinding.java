package me.Azz_9.flex_hud.client.utils;

import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;

@FunctionalInterface
public interface BoolBinding {
	boolean get();

	static BoolBinding and(BoolBinding a, BoolBinding b) {
		return () -> a.get() && b.get();
	}

	static BoolBinding and(ConfigBoolean a, ConfigBoolean b) {
		return () -> a.getValue() && b.getValue();
	}

	static BoolBinding and(ConfigBoolean... configBooleans) {
		return () -> {
			for (ConfigBoolean config : configBooleans) {
				if (!config.getValue()) {
					return false;
				}
			}
			return true;
		};
	}

	static BoolBinding or(BoolBinding a, BoolBinding b) {
		return () -> a.get() || b.get();
	}

	static BoolBinding or(ConfigBoolean a, ConfigBoolean b) {
		return () -> a.getValue() || b.getValue();
	}

	static BoolBinding not(ConfigBoolean a) {
		return () -> !a.getValue();
	}
}