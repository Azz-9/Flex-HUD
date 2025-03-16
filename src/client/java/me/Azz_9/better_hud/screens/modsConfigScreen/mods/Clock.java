package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Clock extends ModsConfigAbstract {
	public Clock(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.clock"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.clock.config.enable"), INSTANCE.clock.enabled, true,
				toggled -> INSTANCE.clock.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.global.config.text_shadow"), INSTANCE.clock.shadow, true,
				toggled -> INSTANCE.clock.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.translatable("better_hud.global.config.text_color"), INSTANCE.clock.color, 0xffffff,
				color -> INSTANCE.clock.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.clock.config.24-hour_format"), INSTANCE.clock.isTwentyFourHourFormat, true,
				toggled -> INSTANCE.clock.isTwentyFourHourFormat = toggled);

		addTextField(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), 80, Text.translatable("better_hud.clock.config.text_format"), INSTANCE.clock.textFormat, "hh:mm:ss",
				text -> INSTANCE.clock.textFormat = text,
				textFormat -> {
					try {
						textFormat = textFormat.toLowerCase();
						if (ModConfig.getInstance().clock.isTwentyFourHourFormat) {
							textFormat = textFormat.replace("hh", "HH").replace("h", "HH");
						} else {
							textFormat += " a";
						}
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern(textFormat);
						LocalTime.now().format(formatter);
						return true;

					} catch (IllegalArgumentException | DateTimeException e) {
						return false;
					}
				});
	}
}
