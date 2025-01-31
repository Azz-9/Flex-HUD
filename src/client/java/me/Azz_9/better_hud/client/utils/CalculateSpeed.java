package me.Azz_9.better_hud.client.utils;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;

import static me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods.Speedometer.SpeedometerUnits.*;

public class CalculateSpeed {
	private static Vec3d previousPosition = null;
	private static double speed = 0.0;

	public static void calculateSpeed(PlayerEntity player) {
		Vec3d currentPosition = player.getPos();
		if (previousPosition != null) {
			Vec3d currentVector = new Vec3d(
					currentPosition.x - previousPosition.x,
					currentPosition.y - previousPosition.y,
					currentPosition.z - previousPosition.z
			);
			speed = Math.sqrt(Math.pow(currentVector.x, 2) + Math.pow(currentVector.y, 2) + Math.pow(currentVector.z, 2)) * 20; // speed in blocks per seconds

			if (ModConfig.getInstance().speedometerUnits == KNOT || (ModConfig.getInstance().useKnotInBoat && player.getVehicle() instanceof BoatEntity)) {
				speed = speed * 1.9438452492;
			} else if (ModConfig.getInstance().speedometerUnits == KPH) {
				speed = speed * 3.6;
			} else if (ModConfig.getInstance().speedometerUnits == MPH) {
				speed = speed * 2.2369362921;
			} // no need MPS because speed is already in meters per seconds

		}
		previousPosition = currentPosition;
	}

	public static double getSpeed() {
		return speed;
	}
}
