package me.Azz_9.flex_hud.client.utils;

import me.Azz_9.flex_hud.client.Flex_hudClient;

import java.awt.*;

public class ChromaColorUtils {
	private static int color;
	private static final int CYCLE_DURATION = 4000;

	public static int getColor() {
		return color;
	}

	public static void updateColor() {
		long elapsedTime = System.currentTimeMillis() - Flex_hudClient.getLaunchTime();

		// Conversion en teinte (Hue) : 0.0 -> 1.0 correspond à 0° -> 360°
		float hue = (elapsedTime % CYCLE_DURATION) / (float) CYCLE_DURATION; // `hue` est compris entre 0 et 1

		// Génération de la couleur RGB à partir du Hue
		color = Color.HSBtoRGB(hue, 1.0f, 1.0f) | 0xff000000;
	}
}
