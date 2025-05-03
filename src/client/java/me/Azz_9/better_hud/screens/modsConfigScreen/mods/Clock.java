package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import me.Azz_9.better_hud.screens.widgets.buttons.CustomToggleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static net.minecraft.text.Text.translatable;

public class Clock extends ModsConfigAbstract {
	public Clock(Screen parent, double scrollAmount) {
		super(translatable("better_hud.clock"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(180);
		}

		addToggleButton(translatable("better_hud.clock.config.enable"), INSTANCE.clock.enabled, true,
				toggled -> INSTANCE.clock.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.clock.shadow, true,
				toggled -> INSTANCE.clock.shadow = toggled);

		CustomToggleButtonWidget toggleButton = addToggleButton(translatable("better_hud.global.config.chroma_text_color"), INSTANCE.clock.chromaColor, false,
				toggled -> INSTANCE.clock.chromaColor = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.clock.color, 0xffffff,
				color -> INSTANCE.clock.color = color, toggleButton, true));

		toggleButton = addToggleButton(translatable("better_hud.global.config.show_background"), INSTANCE.clock.drawBackground, false,
				toggled -> INSTANCE.clock.drawBackground = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.background_color"), INSTANCE.clock.backgroundColor, 0x313131,
				color -> INSTANCE.clock.backgroundColor = color, toggleButton, false));

		addToggleButton(translatable("better_hud.clock.config.24-hour_format"), INSTANCE.clock.isTwentyFourHourFormat, true,
				toggled -> INSTANCE.clock.isTwentyFourHourFormat = toggled);

		addTextField(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), 80, translatable("better_hud.clock.config.text_format"), INSTANCE.clock.textFormat, "hh:mm:ss",
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
