package me.Azz_9.flex_hud.compat;

import net.fabricmc.loader.api.FabricLoader;

public class CompatManager {
	public static boolean isXaeroMinimapLoaded() {
		return FabricLoader.getInstance().isModLoaded("xaerominimap");
	}
}
