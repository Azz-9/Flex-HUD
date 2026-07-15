package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;

import java.awt.*;

import me.Azz_9.flex_hud.CommonClass;

public class ChromaColorTickable implements Tickable {
	private static int color;
	private static final int CYCLE_DURATION = 4000;

	static {
		TickRegistry.register(new ChromaColorTickable());
	}

	public static int getColor() {
		return color;
	}

	public void tick(Minecraft minecraft) {
		long elapsedTime = System.currentTimeMillis() - CommonClass.getLaunchTime();

		// Conversion en teinte (Hue) : 0.0 -> 1.0 correspond à 0° -> 360°
		float hue = (elapsedTime % CYCLE_DURATION) / (float) CYCLE_DURATION; // `hue` est compris entre 0 et 1

		// Génération de la couleur RGB à partir du Hue
		color = ARGB.color(0xff, Color.HSBtoRGB(hue, 1.0f, 1.0f));
	}
}
