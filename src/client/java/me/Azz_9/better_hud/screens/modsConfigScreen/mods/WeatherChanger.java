package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class WeatherChanger extends ModsConfigAbstract {

	public WeatherChanger(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.weather_changer"), parent, scrollAmount);
	}

	public enum Weather {
		Clear,
		Rain,
		Thunder
	}

	@Override
	protected void init() {
		super.init();

		setButtonWidth(170);

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.weather_changer.config.enable"), INSTANCE.weatherChanger.enabled, true,
				toggled -> INSTANCE.weatherChanger.enabled = toggled);
		addCyclingStringButton(getCenterX(), startY + 30, getButtonWidth(), Text.translatable("better_hud.weather_changer.config.selected_weather"), Weather.class, INSTANCE.weatherChanger.selectedWeather, Weather.Clear,
				value -> INSTANCE.weatherChanger.selectedWeather = value);
	}
}
