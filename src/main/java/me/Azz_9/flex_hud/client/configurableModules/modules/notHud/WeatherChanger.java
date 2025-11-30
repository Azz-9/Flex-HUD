package me.Azz_9.flex_hud.client.configurableModules.modules.notHud;

import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class WeatherChanger extends AbstractModule {
	public final ConfigEnum<Weather> selectedWeather = new ConfigEnum<>(Weather.class, Weather.CLEAR, "flex_hud.weather_changer.config.selected_weather");

	public WeatherChanger() {
		this.enabled.setConfigTextTranslationKey("flex_hud.weather_changer.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);

		ConfigRegistry.register(getID(), "selectedWeather", selectedWeather);
	}

	@Override
	public String getID() {
		return "weather_changer";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.weather_changer");
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
								.build()
				);
				this.addAllEntries(
						new CyclingButtonEntry.Builder<Weather>()
								.setCyclingButtonWidth(80)
								.setVariable(selectedWeather)
								.addDependency(this.getConfigList().getFirstEntry(), false)
								.build()
				);
			}
		};
	}

	public enum Weather implements Translatable {
		CLEAR("flex_hud.enum.weather.clear"),
		RAIN("flex_hud.enum.weather.rain"),
		THUNDER("flex_hud.enum.weather.thunder");

		private final String translationKey;

		Weather(String translationKey) {
			this.translationKey = translationKey;
		}

		public String getTranslationKey() {
			return translationKey;
		}
	}
}
