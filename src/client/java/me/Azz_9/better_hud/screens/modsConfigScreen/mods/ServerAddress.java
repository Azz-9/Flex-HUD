package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ServerAddress extends ModsConfigTemplate {
	public ServerAddress(Screen parent, double scrollAmount) {
		super(Text.literal("Server Address"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show server address"), INSTANCE.serverAddress.enabled, true,
				toggled -> INSTANCE.serverAddress.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.serverAddress.shadow, true,
				toggled -> INSTANCE.serverAddress.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.serverAddress.color, 0xffffff,
				color -> INSTANCE.serverAddress.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Hide when offline"), INSTANCE.serverAddress.hideWhenOffline, true,
				toggled -> INSTANCE.serverAddress.hideWhenOffline = toggled);
	}
}
