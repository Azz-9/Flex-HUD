package me.Azz_9.better_hud.client.configurableMods.mods.notHud;

import me.Azz_9.better_hud.client.configurableMods.mods.abstractMod;
import me.Azz_9.better_hud.client.configurableMods.mods.hud.Translatable;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class WeatherChanger extends abstractMod {
	public Weather selectedWeather = Weather.CLEAR;

	public WeatherChanger() {
		this.enabled = false;
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(Text.translatable("better_hud.weather_changer"), parent, parentScrollAmount) {
			@Override
			protected void init() {
				buttonWidth = 180;

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(enabled)
								.setDefaultValue(false)
								.setOnToggle(toggled -> enabled = toggled)
								.setText(Text.translatable("better_hud.weather_changer.config.enable"))
								.build(),
						new CyclingButtonEntry.Builder<Weather>()
								.setCyclingButtonWidth(80)
								.setValue(selectedWeather)
								.setDefaultValue(Weather.CLEAR)
								.setOnValueChange(value -> selectedWeather = value)
								.setText(Text.translatable("better_hud.weather_changer.config.selected_weather"))
								.build()
				);
			}
		};
	}

	public enum Weather implements Translatable {
		CLEAR("better_hud.enum.weather.clear"),
		RAIN("better_hud.enum.weather.rain"),
		THUNDER("better_hud.enum.weather.thunder");

		private final String translationKey;

		Weather(String translationKey) {
			this.translationKey = translationKey;
		}

		public String getTranslationKey() {
			return translationKey;
		}
	}
}
