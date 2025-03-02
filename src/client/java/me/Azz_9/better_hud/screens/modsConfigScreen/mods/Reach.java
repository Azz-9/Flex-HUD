package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Reach extends ModsConfigTemplate {
	public Reach(Screen parent, double scrollAmount) {
		super(Text.literal("Reach Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show reach"), INSTANCE.reach.enabled, true,
				toggled -> INSTANCE.reach.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.reach.shadow, true,
				toggled -> INSTANCE.reach.shadow = toggled);

		addIntField(getCenterX(), startY + 60, getButtonWidth(), Text.of("Number of digits"), INSTANCE.reach.digits, 2, 0, 16,
				value -> INSTANCE.reach.digits = value);

		addColorButton(getCenterX(), startY + 90, getButtonWidth(), Text.of("Text color"), INSTANCE.reach.color, 0xffffff,
				color -> INSTANCE.reach.color = color);
	}
}
