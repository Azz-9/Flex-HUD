package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class WeatherChanger extends ModsConfigAbstract {

	public WeatherChanger(Screen parent, double scrollAmount) {
		super(translatable("better_hud.weather_changer"), parent, scrollAmount);
	}

	public enum Weather {
		Clear,
		Rain,
		Thunder
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(180);
		}

		addToggleButton(translatable("better_hud.weather_changer.config.enable"), INSTANCE.weatherChanger.enabled, true,
				toggled -> INSTANCE.weatherChanger.enabled = toggled);
		addCyclingStringButton(getCenterX(), startY + 30, getButtonWidth(), translatable("better_hud.weather_changer.config.selected_weather"), Weather.class, INSTANCE.weatherChanger.selectedWeather, Weather.Clear,
				value -> INSTANCE.weatherChanger.selectedWeather = value);
	}
}
