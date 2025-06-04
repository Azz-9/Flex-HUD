package me.Azz_9.better_hud.client.configurableMods.mods;

import me.Azz_9.better_hud.client.configurableMods.Configurable;

public abstract class abstractMod implements Configurable {
	public boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}
}
