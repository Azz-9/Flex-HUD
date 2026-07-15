package me.Azz_9.flex_hud.client.modules.notHud;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import me.Azz_9.flex_hud.client.Translatable;
import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigEnum;
import me.Azz_9.flex_hud.client.gui.components.config.entries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.gui.components.config.entries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.modules.AbstractModule;

public class WeatherChanger extends AbstractModule {
	public final ConfigEnum<Weather> selectedWeather = new ConfigEnum<>(Weather.class, Weather.CLEAR, "flex_hud.weather_changer.config.selected_weather");

	public WeatherChanger() {
		super("weather_changer");
		this.enabled.setConfigTextTranslationKey("flex_hud.weather_changer.config.enable");

		ConfigRegistry.register(getID(), "selectedWeather", selectedWeather);
	}

	@Override
	public Component getName() {
		return Component.translatable("flex_hud.weather_changer");
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void initContent() {
				buttonWidth = 180;

				super.initContent();

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

		@Override
		public String getTranslationKey() {
			return translationKey;
		}
	}
}
