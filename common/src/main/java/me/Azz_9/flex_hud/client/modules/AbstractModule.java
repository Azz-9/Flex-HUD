package me.Azz_9.flex_hud.client.modules;

import org.jspecify.annotations.NonNull;

import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.Configurable;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;

public abstract class AbstractModule implements Configurable {
	public ConfigBoolean enabled;
	private @NonNull String id;

	public AbstractModule(@NonNull String id) {
		this.enabled = new ConfigBoolean(false, "flex_hud.global.config.enabled");
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
