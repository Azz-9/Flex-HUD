package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class FPS extends ModsConfigTemplate {
	public FPS(Screen parent, double scrollAmount) {
		super(Text.literal("FPS Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show FPS"), INSTANCE.showFPS, true,
				toggled -> INSTANCE.showFPS = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.FPSShadow, true,
				toggled -> INSTANCE.FPSShadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.FPSColor, 0xffffff,
				color -> INSTANCE.FPSColor = color);
	}
}
