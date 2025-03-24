package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class TimeChanger extends ModsConfigAbstract {
	public TimeChanger(Screen parent, double scrollAmount) {
		super(translatable("better_hud.time_changer"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(225);
		} else {
			setButtonWidth(200);
		}

		addToggleButton(translatable("better_hud.time_changer.config.enable"), INSTANCE.timeChanger.enabled, false,
				toggled -> INSTANCE.timeChanger.enabled = toggled);
		addToggleButton(translatable("better_hud.time_changer.config.use_real_time"), INSTANCE.timeChanger.useRealTime, false,
				toggled -> INSTANCE.timeChanger.useRealTime = toggled);

		addIntSlider(getCenterX(), startY + 60, getButtonWidth(), getButtonHeight(), 120, translatable("better_hud.time_changer.config.selected_time"), INSTANCE.timeChanger.selectedTime, 6000, 0, 24000,
				value -> INSTANCE.timeChanger.selectedTime = value, 1000);
	}
}
