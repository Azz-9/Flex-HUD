package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MemoryUsage extends ModsConfigAbstract {

	public MemoryUsage(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.memory_usage"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.memory_usage.config.enable"), INSTANCE.memoryUsage.enabled, true,
				toggled -> INSTANCE.memoryUsage.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.global.config.text_shadow"), INSTANCE.memoryUsage.shadow, true,
				toggled -> INSTANCE.memoryUsage.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.translatable("better_hud.global.config.text_color"), INSTANCE.memoryUsage.color, 0xffffff,
				color -> INSTANCE.memoryUsage.color = color);
	}
}
