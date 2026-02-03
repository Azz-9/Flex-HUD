package me.Azz_9.flex_hud.client.utils.compass;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public class DimensionTracker {
	private static RegistryKey<World> currentDimension = null;
	public static boolean shouldInit = false;

	public static void check() {
		if (CLIENT.world == null) return;

		RegistryKey<World> newDimension = CLIENT.world.getRegistryKey();

		if (currentDimension == null || !currentDimension.equals(newDimension)) {
			shouldInit = true;

			currentDimension = newDimension;
		} else {
			shouldInit = false;
		}
	}
}

