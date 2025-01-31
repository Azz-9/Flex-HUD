package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.DisplayMode;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Coordinates extends ModsConfigTemplate {
	public Coordinates(Screen parent, double scrollAmount) {
		super(Text.literal("Coordinates Mod"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show coordinates"), INSTANCE.showCoordinates, true,
				toggled -> INSTANCE.showCoordinates = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.coordinatesShadow, true,
				toggled -> INSTANCE.coordinatesShadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.coordinatesColor, 0xffffff,
				color -> INSTANCE.coordinatesColor = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Show Y coordinates"), INSTANCE.showYCoordinates, true,
				toggled -> INSTANCE.showYCoordinates = toggled);
		addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), Text.of("Show biome"), INSTANCE.showBiome, true,
				toggled -> INSTANCE.showBiome = toggled);
		addToggleButton(getCenterX(), startY + 150, getButtonWidth(), getButtonHeight(), Text.of("Show direction"), INSTANCE.showCoordinatesDirection, true,
				toggled -> INSTANCE.showCoordinatesDirection = toggled);
		addToggleButton(getCenterX(), startY + 180, getButtonWidth(), getButtonHeight(), Text.of("Direction abreviation"), INSTANCE.coordinatesDirectionAbreviation, true,
				toggled -> INSTANCE.coordinatesDirectionAbreviation = toggled);

		addIntField(getCenterX(), startY + 210, getButtonWidth(), Text.of("Number of digits"), INSTANCE.coordinatesDigits, 0, 0, 14,
				value -> INSTANCE.coordinatesDigits = value);

		addCyclingStringButton(getCenterX(), startY + 240, getButtonWidth(), Text.of("Orientation"), DisplayMode.class, INSTANCE.displayModeCoordinates, DisplayMode.Vertical,
				value -> INSTANCE.displayModeCoordinates = value);
	}
}
