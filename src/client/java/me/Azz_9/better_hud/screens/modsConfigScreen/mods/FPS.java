package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class FPS extends ModsConfigAbstract {
	public FPS(Screen parent, double scrollAmount) {
		super(translatable("better_hud.fps"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(translatable("better_hud.fps.config.enable"), INSTANCE.fps.enabled, true,
				toggled -> INSTANCE.fps.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.fps.shadow, true,
				toggled -> INSTANCE.fps.shadow = toggled);

		addColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.fps.color, 0xffffff,
				color -> INSTANCE.fps.color = color);
	}
}
