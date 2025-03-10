package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class FPS extends ModsConfigTemplate {
	public FPS(Screen parent, double scrollAmount) {
		super(Text.literal("FPS"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show FPS"), INSTANCE.fps.enabled, true,
				toggled -> INSTANCE.fps.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.fps.shadow, true,
				toggled -> INSTANCE.fps.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.fps.color, 0xffffff,
				color -> INSTANCE.fps.color = color);
	}
}
