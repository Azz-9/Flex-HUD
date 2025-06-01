package me.Azz_9.better_hud.client.configurableMods.mods.notHud;

import me.Azz_9.better_hud.client.configurableMods.mods.Mod;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class WeatherChanger extends Mod {
	public Weather selectedWeather;

	public WeatherChanger() {
		this.enabled = false;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(Text.translatable("better_hud.weather_changer"), parent, parentScrollAmount) {
			@Override
			protected void init() {
			}
		};
	}

	public enum Weather {
		CLEAR,
		RAIN,
		THUNDER
	}
}
