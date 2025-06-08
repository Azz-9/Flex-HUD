package me.Azz_9.better_hud.client.utils;

public class StringUtil {
	public static String capitalize(String input) {
		if (input == null || input.isEmpty()) return input;
		return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}
}
