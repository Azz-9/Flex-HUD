package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Ping extends ModsConfigTemplate {
	public Ping(Screen parent, double scrollAmount) {
		super(Text.literal("Ping Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show ping"), INSTANCE.showPing, true,
				toggled -> INSTANCE.showPing = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.pingShadow, true,
				toggled -> INSTANCE.pingShadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.pingColor, 0xffffff,
				color -> INSTANCE.pingColor = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Hide ping when offline"), INSTANCE.hidePingWhenOffline, true,
				toggled -> INSTANCE.hidePingWhenOffline = toggled);
	}
}
