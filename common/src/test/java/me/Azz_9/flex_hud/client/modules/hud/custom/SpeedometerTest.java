package me.Azz_9.flex_hud.client.modules.hud.custom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpeedometerTest {

	@Test
	public void everySpeedUnitsCanBeConverted() {
		for (Speedometer.SpeedometerUnits unit : Speedometer.SpeedometerUnits.values()) {
			Assertions.assertDoesNotThrow(() -> unit.convert(1.0));
		}
	}
}
