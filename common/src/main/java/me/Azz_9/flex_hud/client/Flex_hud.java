package me.Azz_9.flex_hud.client;

import net.minecraft.client.option.KeyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Flex_hud {

	public static final String MOD_ID = "flex_hud";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean isInMoveElementScreen;

	public static KeyBinding openOptionScreenKeyBind;

	private static long launchTime;

	public static void init() {
		launchTime = System.currentTimeMillis();

		LOGGER.info("Flex HUD has started up.");
	}

	public static long getLaunchTime() {
		return launchTime;
	}
}
