package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Ping extends ModsConfigAbstract {
	public Ping(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.ping"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.ping.config.enable"), INSTANCE.ping.enabled, true,
				toggled -> INSTANCE.ping.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.global.config.text_shadow"), INSTANCE.ping.shadow, true,
				toggled -> INSTANCE.ping.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.translatable("better_hud.global.config.text_color"), INSTANCE.ping.color, 0xffffff,
				color -> INSTANCE.ping.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.ping.config.hide_when_offline"), INSTANCE.ping.hideWhenOffline, true,
				toggled -> INSTANCE.ping.hideWhenOffline = toggled);
	}
}
