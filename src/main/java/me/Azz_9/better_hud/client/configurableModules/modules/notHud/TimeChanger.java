package me.Azz_9.better_hud.client.configurableModules.modules.notHud;

import me.Azz_9.better_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.IntSliderEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.time.LocalTime;

public class TimeChanger extends AbstractModule {
	public ConfigInteger selectedTime = new ConfigInteger(6000, "better_hud.time_changer.config.selected_time", 0, 24000);
	public ConfigBoolean useRealTime = new ConfigBoolean(false, "better_hud.time_changer.config.use_real_time");

	public TimeChanger() {
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);
	}

	@Override
	public void init() {
		this.enabled.setConfigTextTranslationKey("better_hud.time_changer.config.enable");
	}

	@Override
	public String getID() {
		return "time_changer";
	}

	@Override
	public Text getName() {
		return Text.translatable("better_hud.time_changer");
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
		return new AbstractConfigurationScreen(getName(), parent) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 200;
				} else {
					buttonWidth = 155;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(enabled)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(useRealTime)
								.build(),
						new IntSliderEntry.Builder()
								.setIntSliderWidth(80)
								.setVariable(selectedTime)
								.setStep(1000)
								.build()
				);
			}
		};
	}
}
