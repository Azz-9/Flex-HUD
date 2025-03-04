package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class TimeChanger extends ModsConfigTemplate {
	public TimeChanger(Screen parent, double scrollAmount) {
		super(Text.literal("Time Changer"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		setButtonWidth(200);

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Enable time changer"), INSTANCE.timeChanger.enabled, false,
				toggled -> INSTANCE.timeChanger.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Use real time"), INSTANCE.timeChanger.useRealTime, false,
				toggled -> INSTANCE.timeChanger.useRealTime = toggled);

		addIntSlider(getCenterX(), startY + 60, getButtonWidth(), getButtonHeight(), 120, Text.of("Selected time"), INSTANCE.timeChanger.selectedTime, 6000, 0, 24000,
				value -> INSTANCE.timeChanger.selectedTime = value, 1000);
	}
}
