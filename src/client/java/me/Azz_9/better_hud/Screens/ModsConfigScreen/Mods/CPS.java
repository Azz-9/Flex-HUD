package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CPS extends ModsConfigTemplate {

	public CPS(Screen parent, double scrollAmount) {
		super(Text.literal("CPS Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show cps"), INSTANCE.showCps, true,
				toggled -> INSTANCE.showCps = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.cpsShadow, true,
				toggled -> INSTANCE.cpsShadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.cpsColor, 0xffffff,
				color -> INSTANCE.cpsColor = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Show left click cps"), INSTANCE.showLeftClickCPS, true,
				toggled -> INSTANCE.showLeftClickCPS = toggled);
		addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), Text.of("Show right click cps"), INSTANCE.showRightClickCPS, true,
				toggled -> INSTANCE.showRightClickCPS = toggled);
	}
}
