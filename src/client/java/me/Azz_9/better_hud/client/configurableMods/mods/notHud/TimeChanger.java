package me.Azz_9.better_hud.client.configurableMods.mods.notHud;

import me.Azz_9.better_hud.client.configurableMods.mods.Mod;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import net.minecraft.client.gui.screen.Screen;

import java.time.LocalTime;

public class TimeChanger extends Mod {
	public int selectedTime = 6000;
	public boolean useRealTime = false;

	public TimeChanger() {
		this.enabled = false;
	}

	public static long getRealTimeAsMinecraftTime() {
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

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return null;
	}
}
