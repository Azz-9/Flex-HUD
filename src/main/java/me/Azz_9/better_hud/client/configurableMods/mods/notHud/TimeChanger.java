package me.Azz_9.better_hud.client.configurableMods.mods.notHud;

import me.Azz_9.better_hud.client.configurableMods.mods.abstractMod;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.IntSliderEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.time.LocalTime;

public class TimeChanger extends abstractMod {
	public int selectedTime = 6000;
	public boolean useRealTime = false;

	public TimeChanger() {
		this.enabled = false;
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
	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(Text.translatable("better_hud.time_changer"), parent, parentScrollAmount) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 225;
				} else {
					buttonWidth = 200;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(enabled)
								.setDefaultValue(false)
								.setOnToggle(toggled -> enabled = toggled)
								.setText(Text.translatable("better_hud.time_changer.config.enable"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(useRealTime)
								.setDefaultValue(false)
								.setOnToggle(toggled -> useRealTime = toggled)
								.setText(Text.translatable("better_hud.time_changer.config.use_real_time"))
								.build(),
						new IntSliderEntry.Builder()
								.setIntSliderWidth(80)
								.setValue(selectedTime)
								.setMin(0)
								.setMax(24000)
								.setStep(1000)
								.setDefaultValue(6000)
								.setOnValueChange(value -> selectedTime = value)
								.setText(Text.translatable("better_hud.time_changer.config.selected_time"))
								.build()
				);
			}
		};
	}
}
