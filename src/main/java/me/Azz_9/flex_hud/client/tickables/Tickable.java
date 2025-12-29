package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.Minecraft;

public interface Tickable {

	default boolean shouldTick() {
		return true;
	}

	void tick(Minecraft minecraft);
}
