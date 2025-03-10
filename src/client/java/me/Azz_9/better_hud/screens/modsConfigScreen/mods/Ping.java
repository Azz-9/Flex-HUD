package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Ping extends ModsConfigTemplate {
	public Ping(Screen parent, double scrollAmount) {
		super(Text.literal("Ping"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show ping"), INSTANCE.ping.enabled, true,
				toggled -> INSTANCE.ping.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.ping.shadow, true,
				toggled -> INSTANCE.ping.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.ping.color, 0xffffff,
				color -> INSTANCE.ping.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Hide ping when offline"), INSTANCE.ping.hideWhenOffline, true,
				toggled -> INSTANCE.ping.hideWhenOffline = toggled);
	}
}
