package me.Azz_9.flex_hud.client;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class DimensionTracker {
	private static ResourceKey<Level> currentDimension = null;
	public static boolean shouldInit = false;

	public static void check() {
		if (MINECRAFT.level == null) return;

		ResourceKey<Level> newDimension = MINECRAFT.level.dimension();

		if (currentDimension == null || !currentDimension.equals(newDimension)) {
			shouldInit = true;

			currentDimension = newDimension;
		} else {
			shouldInit = false;
		}
	}
}

