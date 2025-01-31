package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Direction extends ModsConfigTemplate {

	public Direction(Screen parent, double scrollAmount) {
		super(Text.literal("Direction Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		setButtonWidth(200);

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show direction"), INSTANCE.showDirection, true,
				toggled -> INSTANCE.showDirection = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.directionShadow, true,
				toggled -> INSTANCE.directionShadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.directionColor, 0xffffff,
				color -> INSTANCE.directionColor = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Show marker"), INSTANCE.showDirectionMarker, true,
				toggled -> INSTANCE.showDirectionMarker = toggled);
        addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), Text.of("Show intermediate points"), INSTANCE.showIntermediateDirectionPoint, true,
				toggled -> INSTANCE.showIntermediateDirectionPoint = toggled);
		addToggleButton(getCenterX(), startY + 150, getButtonWidth(), getButtonHeight(), Text.of("Show Xaero's map waypoints"), INSTANCE.showXaerosMapWaypoints, true,
				toggled -> INSTANCE.showXaerosMapWaypoints = toggled);
	}
}
