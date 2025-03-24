package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class DayCounter extends ModsConfigAbstract {

	public DayCounter(Screen parent, double scrollAmount) {
		super(translatable("better_hud.day_counter"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(160);
		}

		addToggleButton(translatable("better_hud.day_counter.config.enable"), INSTANCE.dayCounter.enabled, true,
				toggled -> INSTANCE.dayCounter.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.dayCounter.shadow, true,
				toggled -> INSTANCE.dayCounter.shadow = toggled);

		addColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.dayCounter.color, 0xffffff,
				color -> INSTANCE.dayCounter.color = color);
	}
}
