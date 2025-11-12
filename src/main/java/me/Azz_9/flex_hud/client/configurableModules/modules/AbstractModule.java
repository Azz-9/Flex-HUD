package me.Azz_9.flex_hud.client.configurableModules.modules;

import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.Configurable;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;

public abstract class AbstractModule implements Configurable {
	public ConfigBoolean enabled;

	public AbstractModule() {
		this.enabled = new ConfigBoolean(true, "enabled");

		ConfigRegistry.register(getID(), "enabled", enabled);
	}

	public boolean isEnabled() {
		return enabled.getValue();
	}

	public void setEnabled(boolean enabled) {
		this.enabled.setValue(enabled);
	}

	public void init() {
	}
}
