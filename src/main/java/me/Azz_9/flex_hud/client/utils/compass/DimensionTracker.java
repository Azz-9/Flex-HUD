package me.Azz_9.flex_hud.client.utils.compass;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class DimensionTracker {
	private static ResourceKey<Level> currentDimension = null;
	public static boolean shouldInit = false;

	public static void check() {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft.level == null) return;

		ResourceKey<Level> newDimension = minecraft.level.dimension();

		if (currentDimension == null || !currentDimension.equals(newDimension)) {
			shouldInit = true;

			currentDimension = newDimension;
		} else {
			shouldInit = false;
		}
	}
}

