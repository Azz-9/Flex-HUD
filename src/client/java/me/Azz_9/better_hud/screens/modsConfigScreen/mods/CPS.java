package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CPS extends ModsConfigAbstract {

	public CPS(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.cps"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.cps.config.enable"), INSTANCE.cps.enabled, true,
				toggled -> INSTANCE.cps.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.global.config.text_shadow"), INSTANCE.cps.shadow, true,
				toggled -> INSTANCE.cps.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.translatable("better_hud.global.config.text_color"), INSTANCE.cps.color, 0xffffff,
				color -> INSTANCE.cps.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.cps.config.show_left_click"), INSTANCE.cps.showLeftClick, true,
				toggled -> INSTANCE.cps.showLeftClick = toggled);
		addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.cps.config.show_right_click"), INSTANCE.cps.showRightClick, true,
				toggled -> INSTANCE.cps.showRightClick = toggled);
	}
}
