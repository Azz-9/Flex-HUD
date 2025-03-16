package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Direction extends ModsConfigAbstract {

	public Direction(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.direction"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		setButtonWidth(200);

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.direction.config.enable"), INSTANCE.direction.enabled, true,
				toggled -> INSTANCE.direction.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.global.config.text_shadow"), INSTANCE.direction.shadow, true,
				toggled -> INSTANCE.direction.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.translatable("better_hud.global.config.text_color"), INSTANCE.direction.color, 0xffffff,
				color -> INSTANCE.direction.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.direction.config.show_marker"), INSTANCE.direction.showMarker, true,
				toggled -> INSTANCE.direction.showMarker = toggled);
		addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.direction.config.show_intermediate_point"), INSTANCE.direction.showIntermediatePoint, true,
				toggled -> INSTANCE.direction.showIntermediatePoint = toggled);
		addToggleButton(getCenterX(), startY + 150, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.direction.config.show_xaeros_map_waypoints"), INSTANCE.direction.showXaerosMapWaypoints, true,
				toggled -> INSTANCE.direction.showXaerosMapWaypoints = toggled);
	}
}
