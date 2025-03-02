package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Playtime extends ModsConfigTemplate {
	public Playtime(Screen parent, double scrollAmount) {
		super(Text.literal("Playtime Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show playtime"), INSTANCE.playtime.enabled, true,
				toggled -> INSTANCE.playtime.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.playtime.shadow, true,
				toggled -> INSTANCE.playtime.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.playtime.color, 0xffffff,
				color -> INSTANCE.playtime.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Show prefix"), INSTANCE.playtime.showPrefix, true,
				toggled -> INSTANCE.playtime.showPrefix = toggled);
	}
}
