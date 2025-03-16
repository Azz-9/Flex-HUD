package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Optional;

public class Speedometer extends ModsConfigAbstract {
	public Speedometer(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.speedometer"), parent, scrollAmount);
	}

	public enum SpeedometerUnits {
		MPS,
		KPH,
		MPH,
		KNOT
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.speedometer.config.enable"), INSTANCE.speedometer.enabled, true,
				toggled -> INSTANCE.speedometer.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.global.config.text_shadow"), INSTANCE.speedometer.shadow, true,
				toggled -> INSTANCE.speedometer.shadow = toggled);

		addIntField(getCenterX(), startY + 60, getButtonWidth(), Text.translatable("better_hud.speedometer.config.number_of_digits"), INSTANCE.speedometer.digits, 1, 0, 16,
				value -> INSTANCE.speedometer.digits = value);

		addCyclingStringButton(getCenterX(), startY + 90, getButtonWidth(), Text.translatable("better_hud.speedometer.config.selected_unit"), SpeedometerUnits.class, INSTANCE.speedometer.units, SpeedometerUnits.MPS,
				value -> INSTANCE.speedometer.units = value,
				value -> switch (value) {
					case SpeedometerUnits.MPS ->
							Optional.of(new Text[]{Text.translatable("better_hud.speedometer.config.tooltip.mps")});
					case SpeedometerUnits.KPH ->
							Optional.of(new Text[]{Text.translatable("better_hud.speedometer.config.tooltip.kph")});
					case SpeedometerUnits.MPH ->
							Optional.of(new Text[]{Text.translatable("better_hud.speedometer.config.tooltip.mph")});
					default -> Optional.empty();
				});

		addColorButton(getCenterX(), startY + 120, getButtonWidth(), Text.translatable("better_hud.global.config.text_color"), INSTANCE.speedometer.color, 0xffffff,
				color -> INSTANCE.speedometer.color = color);
	}
}
