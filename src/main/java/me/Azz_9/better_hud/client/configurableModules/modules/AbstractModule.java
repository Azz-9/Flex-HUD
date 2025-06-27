package me.Azz_9.better_hud.client.configurableModules.modules;

import me.Azz_9.better_hud.client.configurableModules.Configurable;

public abstract class AbstractModule implements Configurable {
	public boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}
}
