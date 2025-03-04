package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class WeatherChanger extends ModsConfigTemplate {

	public WeatherChanger(Screen parent, double scrollAmount) {
		super(Text.literal("Weather Changer Mod"), parent, scrollAmount);
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

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Enable weather changer"), INSTANCE.weatherChanger.enabled, true,
				toggled -> INSTANCE.weatherChanger.enabled = toggled);
		addCyclingStringButton(getCenterX(), startY + 30, getButtonWidth(), Text.of("Selected weather"), Weather.class, INSTANCE.weatherChanger.selectedWeather, Weather.Clear,
				value -> INSTANCE.weatherChanger.selectedWeather = value);
	}
}
