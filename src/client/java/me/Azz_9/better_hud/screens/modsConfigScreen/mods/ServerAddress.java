package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ServerAddress extends ModsConfigAbstract {
	public ServerAddress(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.server_address"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.server_address.config.enable"), INSTANCE.serverAddress.enabled, true,
				toggled -> INSTANCE.serverAddress.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.global.config.text_shadow"), INSTANCE.serverAddress.shadow, true,
				toggled -> INSTANCE.serverAddress.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.translatable("better_hud.global.config.text_color"), INSTANCE.serverAddress.color, 0xffffff,
				color -> INSTANCE.serverAddress.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.server_address.config.hide_when_offline"), INSTANCE.serverAddress.hideWhenOffline, true,
				toggled -> INSTANCE.serverAddress.hideWhenOffline = toggled);
	}
}
