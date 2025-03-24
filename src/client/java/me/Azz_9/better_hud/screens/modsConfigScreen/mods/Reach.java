package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class Reach extends ModsConfigAbstract {
	public Reach(Screen parent, double scrollAmount) {
		super(translatable("better_hud.reach"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(translatable("better_hud.reach.config.enable"), INSTANCE.reach.enabled, true,
				toggled -> INSTANCE.reach.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.reach.shadow, true,
				toggled -> INSTANCE.reach.shadow = toggled);

		addIntField(translatable("better_hud.reach.config.number_of_digits"), INSTANCE.reach.digits, 2, 0, 16,
				value -> INSTANCE.reach.digits = value);

		addColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.reach.color, 0xffffff,
				color -> INSTANCE.reach.color = color);
	}
}
