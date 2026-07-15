package me.Azz_9.flex_hud;

import static me.Azz_9.flex_hud.Constants.MOD_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FlexHudLogger {
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	private static final String PREFIX = "[" + MOD_NAME + "] ";

	public static void info(String message, Object... args) {
		LOGGER.info(PREFIX + message, args);
	}

	public static void warn(String message, Object... args) {
		LOGGER.warn(PREFIX + message, args);
	}

	public static void error(String message, Object... args) {
		LOGGER.error(PREFIX + message, args);
	}

	public static void debug(String message, Object... args) {
		LOGGER.debug(PREFIX + message, args);
	}
}
