package me.Azz_9.better_hud.client.configurableMods.mods;

import me.Azz_9.better_hud.client.configurableMods.Configurable;

public abstract class Mod implements Configurable {
	public boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}
}
