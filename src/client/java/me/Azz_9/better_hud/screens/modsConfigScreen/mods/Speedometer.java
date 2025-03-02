package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Optional;

public class Speedometer extends ModsConfigTemplate {
	public Speedometer(Screen parent, double scrollAmount) {
		super(Text.literal("Speedometer Mod"), parent, scrollAmount);
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

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show speedometer"), INSTANCE.speedometer.enabled, true,
				toggled -> INSTANCE.speedometer.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.speedometer.shadow, true,
				toggled -> INSTANCE.speedometer.shadow = toggled);

		addIntField(getCenterX(), startY + 60, getButtonWidth(), Text.of("Number of digits"), INSTANCE.speedometer.digits, 1, 0, 16,
				value -> INSTANCE.speedometer.digits = value);

		addCyclingStringButton(getCenterX(), startY + 90, getButtonWidth(), Text.of("Speed unit"), SpeedometerUnits.class, INSTANCE.speedometer.units, SpeedometerUnits.MPS,
				value -> INSTANCE.speedometer.units = value,
				value -> switch (value) {
											case SpeedometerUnits.MPS -> Optional.of(new Text[]{Text.literal("Meters per second (m/s):")});
											case SpeedometerUnits.KPH -> Optional.of(new Text[]{Text.literal("Kilometers per hour (km/h):")});
											case SpeedometerUnits.MPH -> Optional.of(new Text[]{Text.literal("Miles per hour (mph):")});
											default -> Optional.empty();
										});

		addColorButton(getCenterX(), startY + 120, getButtonWidth(), Text.of("Text color"), INSTANCE.speedometer.color, 0xffffff,
				color -> INSTANCE.speedometer.color = color);
	}
}
