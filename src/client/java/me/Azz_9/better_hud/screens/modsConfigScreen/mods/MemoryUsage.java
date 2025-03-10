package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MemoryUsage extends ModsConfigTemplate {

	public MemoryUsage(Screen parent, double scrollAmount) {
		super(Text.literal("Memory Usage"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show memory usage"), INSTANCE.memoryUsage.enabled, true,
				toggled -> INSTANCE.memoryUsage.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.memoryUsage.shadow, true,
				toggled -> INSTANCE.memoryUsage.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.memoryUsage.color, 0xffffff,
				color -> INSTANCE.memoryUsage.color = color);
	}
}
