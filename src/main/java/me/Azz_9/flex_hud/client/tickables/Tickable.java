package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.MinecraftClient;

public interface Tickable {

	default boolean shouldTick() {
		return true;
	}

	void tick(MinecraftClient client);
}
