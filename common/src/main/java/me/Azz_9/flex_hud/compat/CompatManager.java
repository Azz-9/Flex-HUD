package me.Azz_9.flex_hud.compat;

import static me.Azz_9.flex_hud.Constants.JOURNEY_MAP_ID;
import static me.Azz_9.flex_hud.Constants.XAEROMINIMAP_ID;

import me.Azz_9.flex_hud.platform.Services;

public class CompatManager {
	public static boolean isXaeroMinimapLoaded() {
		return Services.PLATFORM.isModLoaded(XAEROMINIMAP_ID);
	}

	public static boolean isJourneyMapLoaded() {
		return Services.PLATFORM.isModLoaded(JOURNEY_MAP_ID);
	}
}
