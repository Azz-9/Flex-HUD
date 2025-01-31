package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class DayCounter extends ModsConfigTemplate {

	public DayCounter(Screen parent, double scrollAmount) {
		super(Text.literal("Day Counter Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show day counter"), INSTANCE.showDayCounter, true,
				toggled -> INSTANCE.showDayCounter = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(),getButtonHeight(), Text.of("Text shadow"), INSTANCE.dayCounterShadow, true,
				toggled -> INSTANCE.dayCounterShadow = toggled);

        addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.dayCounterColor, 0xffffff,
				color -> INSTANCE.dayCounterColor = color);
	}
}
