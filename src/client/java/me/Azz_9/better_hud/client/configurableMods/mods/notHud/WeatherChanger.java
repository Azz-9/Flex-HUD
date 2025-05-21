package me.Azz_9.better_hud.client.configurableMods.mods.notHud;

import me.Azz_9.better_hud.client.configurableMods.mods.Mod;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import net.minecraft.client.gui.screen.Screen;

public class WeatherChanger extends Mod {
	public Weather selectedWeather;

	public WeatherChanger() {
		this.enabled = false;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return null;
	}

	public enum Weather {
		CLEAR,
		RAIN,
		THUNDER
	}
}
