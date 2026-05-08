package me.Azz_9.flex_hud.client.configurableModules.modules;

import org.jspecify.annotations.NonNull;

import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.Configurable;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;

public abstract class AbstractModule implements Configurable {
	public ConfigBoolean enabled;
	private @NonNull String id;

	public AbstractModule(@NonNull String id) {
		this.enabled = new ConfigBoolean(true, "flex_hud.global.config.enabled");
		this.id = id;

		ConfigRegistry.register(getID(), "enabled", enabled);
	}

	@Override
	public boolean isEnabled() {
		return enabled.getValue();
	}

	public void setEnabled(boolean enabled) {
		this.enabled.setValue(enabled);
	}

	@Override
	public final String getID() {
		return id;
	}

	public void setId(@NonNull String id) {
		this.id = id;
	}

	public void init() {
	}
}
