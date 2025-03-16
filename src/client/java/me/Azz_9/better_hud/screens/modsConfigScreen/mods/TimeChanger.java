package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class TimeChanger extends ModsConfigAbstract {
	public TimeChanger(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.time_changer"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		setButtonWidth(200);

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.time_changer.config.enable"), INSTANCE.timeChanger.enabled, false,
				toggled -> INSTANCE.timeChanger.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.time_changer.config.use_real_time"), INSTANCE.timeChanger.useRealTime, false,
				toggled -> INSTANCE.timeChanger.useRealTime = toggled);

		addIntSlider(getCenterX(), startY + 60, getButtonWidth(), getButtonHeight(), 120, Text.translatable("better_hud.time_changer.config.selected_time"), INSTANCE.timeChanger.selectedTime, 6000, 0, 24000,
				value -> INSTANCE.timeChanger.selectedTime = value, 1000);
	}
}
