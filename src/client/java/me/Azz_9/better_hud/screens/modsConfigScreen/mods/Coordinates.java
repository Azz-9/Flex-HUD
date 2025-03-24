package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.DisplayMode;
import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import me.Azz_9.better_hud.screens.widgets.buttons.CustomToggleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class Coordinates extends ModsConfigAbstract {
	public Coordinates(Screen parent, double scrollAmount) {
		super(translatable("better_hud.coordinates"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(165);
		}

		addToggleButton(translatable("better_hud.coordinates.config.enable"), INSTANCE.coordinates.enabled, true,
				toggled -> INSTANCE.coordinates.enabled = toggled);

		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.coordinates.shadow, true,
				toggled -> INSTANCE.coordinates.shadow = toggled);

		addColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.coordinates.color, 0xffffff,
				color -> INSTANCE.coordinates.color = color);

		addToggleButton(translatable("better_hud.coordinates.config.show_y"), INSTANCE.coordinates.showY, true,
				toggled -> INSTANCE.coordinates.showY = toggled);

		addToggleButton(translatable("better_hud.coordinates.config.show_biome"), INSTANCE.coordinates.showBiome, true,
				toggled -> INSTANCE.coordinates.showBiome = toggled);

		CustomToggleButtonWidget toggleButton = addToggleButton(translatable("better_hud.coordinates.config.show_direction"), INSTANCE.coordinates.showDirection, true,
				toggled -> INSTANCE.coordinates.showDirection = toggled);

		addDependentToggleButton(translatable("better_hud.coordinates.config.direction_abbreviation"), INSTANCE.coordinates.directionAbreviation, true,
				toggled -> INSTANCE.coordinates.directionAbreviation = toggled, toggleButton, false);

		addIntField(translatable("better_hud.coordinates.config.number_of_digits"), INSTANCE.coordinates.numberOfDigits, 0, 0, 14,
				value -> INSTANCE.coordinates.numberOfDigits = value);

		addCyclingStringButton(getCenterX(), startY + 240, getButtonWidth(),
				translatable("better_hud.coordinates.config.orientation"), DisplayMode.class, INSTANCE.coordinates.displayMode, DisplayMode.Vertical,
				value -> INSTANCE.coordinates.displayMode = value);
	}
}
