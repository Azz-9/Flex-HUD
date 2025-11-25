package me.Azz_9.flex_hud.client.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;


public class FlexHudLogger {
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void info(String message, Object... args) {
		LOGGER.info("[Flex HUD] " + message, args);
	}

	public static void warn(String message, Object... args) {
		LOGGER.warn("[Flex HUD] " + message, args);
	}

	public static void error(String message, Object... args) {
		LOGGER.error("[Flex HUD] " + message, args);
	}

	public static void debug(String message, Object... args) {
		LOGGER.debug("[Flex HUD] " + message, args);
	}
}
