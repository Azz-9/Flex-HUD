package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.DisplayMode;
import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Coordinates extends ModsConfigAbstract {
	public Coordinates(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.coordinates"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(),
				Text.translatable("better_hud.coordinates.config.enable"), INSTANCE.coordinates.enabled, true,
				toggled -> INSTANCE.coordinates.enabled = toggled);

		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(),
				Text.translatable("better_hud.global.config.text_shadow"), INSTANCE.coordinates.shadow, true,
				toggled -> INSTANCE.coordinates.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(),
				Text.translatable("better_hud.global.config.text_color"), INSTANCE.coordinates.color, 0xffffff,
				color -> INSTANCE.coordinates.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(),
				Text.translatable("better_hud.coordinates.config.show_y"), INSTANCE.coordinates.showY, true,
				toggled -> INSTANCE.coordinates.showY = toggled);

		addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(),
				Text.translatable("better_hud.coordinates.config.show_biome"), INSTANCE.coordinates.showBiome, true,
				toggled -> INSTANCE.coordinates.showBiome = toggled);

		addToggleButton(getCenterX(), startY + 150, getButtonWidth(), getButtonHeight(),
				Text.translatable("better_hud.coordinates.config.show_direction"), INSTANCE.coordinates.showDirection, true,
				toggled -> INSTANCE.coordinates.showDirection = toggled);

		addToggleButton(getCenterX(), startY + 180, getButtonWidth(), getButtonHeight(),
				Text.translatable("better_hud.coordinates.config.direction_abbreviation"), INSTANCE.coordinates.directionAbreviation, true,
				toggled -> INSTANCE.coordinates.directionAbreviation = toggled);

		addIntField(getCenterX(), startY + 210, getButtonWidth(),
				Text.translatable("better_hud.coordinates.config.number_of_digits"), INSTANCE.coordinates.numberOfDigits, 0, 0, 14,
				value -> INSTANCE.coordinates.numberOfDigits = value);

		addCyclingStringButton(getCenterX(), startY + 240, getButtonWidth(),
				Text.translatable("better_hud.coordinates.config.orientation"), DisplayMode.class, INSTANCE.coordinates.displayMode, DisplayMode.Vertical,
				value -> INSTANCE.coordinates.displayMode = value);
	}
}
