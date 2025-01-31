package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ServerAddress extends ModsConfigTemplate {
	public ServerAddress(Screen parent, double scrollAmount) {
		super(Text.literal("Server Address Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show server address"), INSTANCE.showServerAddress, true,
				toggled -> INSTANCE.showServerAddress = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.serverAddressShadow, true,
				toggled -> INSTANCE.serverAddressShadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.serverAddressColor, 0xffffff,
				color -> INSTANCE.serverAddressColor = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Hide when offline"), INSTANCE.hideServerAddressWhenOffline, true,
				toggled -> INSTANCE.hideServerAddressWhenOffline = toggled);
	}
}
