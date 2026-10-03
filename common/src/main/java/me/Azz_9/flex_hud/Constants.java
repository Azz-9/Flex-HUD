package me.Azz_9.flex_hud;

import org.jetbrains.annotations.NotNull;

public class Constants {

	public static final @NotNull String FABRIC = "Fabric";
	public static final @NotNull String NEOFORGE = "NeoForge";

	public static final boolean DEBUG = Boolean.parseBoolean(System.getenv().getOrDefault("FLEXHUD_DEBUG", "false"));

	public static final String MOD_ID = "flex_hud";
	public static final String MOD_NAME = "Flex HUD";

	public static final String XAEROMINIMAP_ID = "xaerominimap";
	public static final String JOURNEY_MAP_ID = "journeymap";
	public static final String SMOOTH_SCROLLING_ID = "smoothscroll";
	public static final String SMOOTH_SCROLLING_REFURBISHED_ID = "smoothscrollingrefurbished";
	public static final String JADE_ID = "jade";
}