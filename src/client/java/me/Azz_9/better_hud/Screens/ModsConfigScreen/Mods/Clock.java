package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Clock extends ModsConfigTemplate {
	public Clock(Screen parent, double scrollAmount) {
		super(Text.literal("Clock Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show clock"), INSTANCE.showClock, true,
				toggled -> INSTANCE.showClock = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.clockShadow, true,
				toggled -> INSTANCE.clockShadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.clockColor, 0xffffff,
				color -> INSTANCE.clockColor = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("24-hour format"), INSTANCE.clock24hourformat, true,
				toggled -> INSTANCE.clock24hourformat = toggled);

		addTextField(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), 80, Text.of("Text format"), INSTANCE.clockTextFormat, "hh:mm:ss",
				text -> INSTANCE.clockTextFormat = text,
				textFormat -> {
					try {
						textFormat = textFormat.toLowerCase();
						if (ModConfig.getInstance().clock24hourformat) {
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
