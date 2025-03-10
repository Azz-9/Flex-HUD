package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Direction extends ModsConfigTemplate {

	public Direction(Screen parent, double scrollAmount) {
		super(Text.literal("Direction"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		setButtonWidth(200);

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show direction"), INSTANCE.direction.enabled, true,
				toggled -> INSTANCE.direction.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.direction.shadow, true,
				toggled -> INSTANCE.direction.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.direction.color, 0xffffff,
				color -> INSTANCE.direction.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Show marker"), INSTANCE.direction.showMarker, true,
				toggled -> INSTANCE.direction.showMarker = toggled);
		addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), Text.of("Show intermediate points"), INSTANCE.direction.showIntermediatePoint, true,
				toggled -> INSTANCE.direction.showIntermediatePoint = toggled);
		addToggleButton(getCenterX(), startY + 150, getButtonWidth(), getButtonHeight(), Text.of("Show Xaero's map waypoints"), INSTANCE.direction.showXaerosMapWaypoints, true,
				toggled -> INSTANCE.direction.showXaerosMapWaypoints = toggled);
	}
}
