package me.Azz_9.flex_hud.compat;

import net.fabricmc.loader.api.FabricLoader;

public class XaeroCompatFabric implements XaeroCompat.XaeroCompatBridge {
	@Override
	public boolean isXaerosMinimapLoaded() {
		return FabricLoader.getInstance().isModLoaded("xaerominimap");
	}
}