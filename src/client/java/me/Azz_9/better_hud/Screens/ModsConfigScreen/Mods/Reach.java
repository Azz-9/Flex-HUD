package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Reach extends ModsConfigTemplate {
	public Reach(Screen parent, double scrollAmount) {
		super(Text.literal("Reach Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show reach"), INSTANCE.showReach, true,
				toggled -> INSTANCE.showReach = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.reachShadow, true,
				toggled -> INSTANCE.reachShadow = toggled);

		addIntField(getCenterX(), startY + 60, getButtonWidth(), Text.of("Number of digits"), INSTANCE.reachDigits, 2, 0, 16,
				value -> INSTANCE.reachDigits = value);

		addColorButton(getCenterX(), startY + 90, getButtonWidth(), Text.of("Text color"), INSTANCE.reachColor, 0xffffff,
				color -> INSTANCE.reachColor = color);
	}
}
