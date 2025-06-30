package me.Azz_9.better_hud.client.configurableModules.modules.notHud;

import me.Azz_9.better_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.better_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class WeatherChanger extends AbstractModule {
	public ConfigEnum<Weather> selectedWeather = new ConfigEnum<>(Weather.CLEAR, "better_hud.weather_changer.config.selected_weather");

	public WeatherChanger() {
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public void init() {
		this.enabled.setConfigTextTranslationKey("better_hud.weather_changer.config.enable");
	}

	@Override
	public String getID() {
		return "weather_changer";
	}

	@Override
	public Text getName() {
		return Text.translatable("better_hud.weather_changer");
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				buttonWidth = 180;

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build(),
						new CyclingButtonEntry.Builder<Weather>()
								.setCyclingButtonWidth(80)
								.setVariable(selectedWeather)
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
