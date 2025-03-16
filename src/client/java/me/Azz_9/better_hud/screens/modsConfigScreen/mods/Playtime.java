package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Playtime extends ModsConfigAbstract {
	public Playtime(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.playtime"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.playtime.config.enable"), INSTANCE.playtime.enabled, true,
				toggled -> INSTANCE.playtime.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.global.config.text_shadow"), INSTANCE.playtime.shadow, true,
				toggled -> INSTANCE.playtime.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.translatable("better_hud.global.config.text_color"), INSTANCE.playtime.color, 0xffffff,
				color -> INSTANCE.playtime.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.playtime.config.show_prefix"), INSTANCE.playtime.showPrefix, true,
				toggled -> INSTANCE.playtime.showPrefix = toggled);
	}
}
