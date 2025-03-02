package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class DayCounter extends ModsConfigTemplate {

	public DayCounter(Screen parent, double scrollAmount) {
		super(Text.literal("Day Counter Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show day counter"), INSTANCE.dayCounter.enabled, true,
				toggled -> INSTANCE.dayCounter.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(),getButtonHeight(), Text.of("Text shadow"), INSTANCE.dayCounter.shadow, true,
				toggled -> INSTANCE.dayCounter.shadow = toggled);

        addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.dayCounter.color, 0xffffff,
				color -> INSTANCE.dayCounter.color = color);
	}
}
