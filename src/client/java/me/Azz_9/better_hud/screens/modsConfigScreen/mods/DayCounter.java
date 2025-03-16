package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class DayCounter extends ModsConfigAbstract {

	public DayCounter(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.day_counter"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.day_counter.config.enable"), INSTANCE.dayCounter.enabled, true,
				toggled -> INSTANCE.dayCounter.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.global.config.text_shadow"), INSTANCE.dayCounter.shadow, true,
				toggled -> INSTANCE.dayCounter.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.translatable("better_hud.global.config.text_color"), INSTANCE.dayCounter.color, 0xffffff,
				color -> INSTANCE.dayCounter.color = color);
	}
}
