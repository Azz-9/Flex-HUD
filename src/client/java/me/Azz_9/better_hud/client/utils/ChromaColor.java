package me.Azz_9.better_hud.client.utils;

import me.Azz_9.better_hud.client.Better_hudClient;

import java.awt.*;

public class ChromaColor {
	private static int color;
	private static final int cycleDuration = 4000;

	public static int getColor() {
		return color;
	}

	public static void updateColor() {
		long elapsedTime = System.currentTimeMillis() - Better_hudClient.getLaunchTime();

		// Conversion en teinte (Hue) : 0.0 -> 1.0 correspond à 0° -> 360°
		float hue = (elapsedTime % cycleDuration) / (float) cycleDuration; // `hue` est compris entre 0 et 1

		// Génération de la couleur RGB à partir du Hue
		color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
	}
}
