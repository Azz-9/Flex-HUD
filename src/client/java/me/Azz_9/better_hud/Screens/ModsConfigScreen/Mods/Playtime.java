package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Playtime extends ModsConfigTemplate {
	public Playtime(Screen parent, double scrollAmount) {
		super(Text.literal("Playtime Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show playtime"), INSTANCE.showPlaytime, true,
				toggled -> INSTANCE.showPlaytime = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.playtimeShadow, true,
				toggled -> INSTANCE.playtimeShadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.playtimeColor, 0xffffff,
				color -> INSTANCE.playtimeColor = color);
	}
}
