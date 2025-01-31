package me.Azz_9.better_hud.client.utils;

import java.time.LocalTime;

public class TimeChanger {
	public static final TimeChanger INSTANCE = new TimeChanger();

	public long getRealTimeAsMinecraftTime() {
		LocalTime realTime = LocalTime.now();

		// Dans Minecraft, un jour dure 24000 ticks
		// Minuit est à 18000, midi est à 6000
		int hour = realTime.getHour();
		int minute = realTime.getMinute();

		// Convertir l'heure réelle en ticks Minecraft
		long minecraftTime = ((hour + 18) % 24) * 1000; // +18 pour aligner minuit à 18000
		minecraftTime += (long) (minute / 60.0 * 1000);

		return minecraftTime;
	}

	public static TimeChanger getInstance() {
		return INSTANCE;
	}
}
