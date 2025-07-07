package me.Azz_9.better_hud.client.utils.compass;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public class DimensionTracker {
	private static RegistryKey<World> currentDimension = null;
	public static boolean shouldInit = false;

	public static void check() {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.world == null) return;

		RegistryKey<World> newDimension = client.world.getRegistryKey();

		if (currentDimension == null || !currentDimension.equals(newDimension)) {
			shouldInit = true;

			currentDimension = newDimension;
		}
	}
}

