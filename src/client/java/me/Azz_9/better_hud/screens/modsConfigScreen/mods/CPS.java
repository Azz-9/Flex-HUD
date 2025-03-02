package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CPS extends ModsConfigTemplate {

	public CPS(Screen parent, double scrollAmount) {
		super(Text.literal("cps Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show cps"), INSTANCE.cps.enabled, true,
				toggled -> INSTANCE.cps.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.cps.shadow, true,
				toggled -> INSTANCE.cps.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.cps.color, 0xffffff,
				color -> INSTANCE.cps.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Show left click cps"), INSTANCE.cps.showLeftClick, true,
				toggled -> INSTANCE.cps.showLeftClick = toggled);
		addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), Text.of("Show right click cps"), INSTANCE.cps.showRightClick, true,
				toggled -> INSTANCE.cps.showRightClick = toggled);
	}
}
