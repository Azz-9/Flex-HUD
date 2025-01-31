package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
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

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show speedometer"), INSTANCE.showSpeedometer, true,
				toggled -> INSTANCE.showSpeedometer = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.speedometerShadow, true,
				toggled -> INSTANCE.speedometerShadow = toggled);

		addIntField(getCenterX(), startY + 60, getButtonWidth(), Text.of("Number of digits"), INSTANCE.speedometerDigits, 1, 0, 16,
				value -> INSTANCE.speedometerDigits = value);

		addCyclingStringButton(getCenterX(), startY + 90, getButtonWidth(), Text.of("Speed unit"), SpeedometerUnits.class, INSTANCE.speedometerUnits, SpeedometerUnits.MPS,
				value -> INSTANCE.speedometerUnits = value,
				value -> switch (value) {
											case SpeedometerUnits.MPS -> Optional.of(new Text[]{Text.literal("Meters per second (m/s):")});
											case SpeedometerUnits.KPH -> Optional.of(new Text[]{Text.literal("Kilometers per hour (km/h):")});
											case SpeedometerUnits.MPH -> Optional.of(new Text[]{Text.literal("Miles per hour (mph):")});
											default -> Optional.empty();
										});

		addColorButton(getCenterX(), startY + 120, getButtonWidth(), Text.of("Text color"), INSTANCE.speedometerColor, 0xffffff,
				color -> INSTANCE.speedometerColor = color);
	}
}
