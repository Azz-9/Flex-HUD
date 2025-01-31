package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MemoryUsage extends ModsConfigTemplate {

	public MemoryUsage(Screen parent, double scrollAmount) {
		super(Text.literal("Memory Usage Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show memory usage"), INSTANCE.showMemoryUsage, true,
				toggled -> INSTANCE.showMemoryUsage = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.memoryUsageShadow, true,
				toggled -> INSTANCE.memoryUsageShadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.memoryUsageColor, 0xffffff,
				color -> INSTANCE.memoryUsageColor = color);
	}
}
