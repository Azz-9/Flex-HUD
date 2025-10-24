package me.Azz_9.flex_hud.client.utils.speedometer;

import me.Azz_9.flex_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.Speedometer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class SpeedUtils {
	private static Vec3d previousPosition = null;
	private static double speed = 0.0;
	private static String formattedSpeed = "";

	public static void calculateSpeed() {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) {
			return;
		}

		Vec3d currentPosition = player.getEntityPos();
		if (previousPosition != null) {
			Vec3d currentVector = new Vec3d(
					currentPosition.x - previousPosition.x,
					currentPosition.y - previousPosition.y,
					currentPosition.z - previousPosition.z
			);
			speed = Math.sqrt(Math.pow(currentVector.x, 2) + Math.pow(currentVector.y, 2) + Math.pow(currentVector.z, 2)) * 20; // speed in blocks per seconds

			if (JsonConfigHelper.getInstance().speedometer.units.getValue() == Speedometer.SpeedometerUnits.KNOT || (JsonConfigHelper.getInstance().speedometer.useKnotInBoat.getValue() && player.getVehicle() instanceof BoatEntity)) {
				speed = speed * 1.9438452492;
			} else if (JsonConfigHelper.getInstance().speedometer.units.getValue() == Speedometer.SpeedometerUnits.KPH) {
				speed = speed * 3.6;
			} else if (JsonConfigHelper.getInstance().speedometer.units.getValue() == Speedometer.SpeedometerUnits.MPH) {
				speed = speed * 2.2369362921;
			} // no need MPS because the speed is already in meters per seconds

		}
		previousPosition = currentPosition;

		formattedSpeed = getString(player);
	}

	private static @NotNull String getString(PlayerEntity player) {
		Speedometer speedometer = JsonConfigHelper.getInstance().speedometer;
		String format = "%." + speedometer.digits.getValue() + "f";
		String formattedSpeed = String.format(format, SpeedUtils.getSpeed());

		if (speedometer.units.getValue() == Speedometer.SpeedometerUnits.KNOT || (speedometer.useKnotInBoat.getValue() && player.getVehicle() instanceof BoatEntity)) {
			formattedSpeed += " " + Text.translatable(Speedometer.SpeedometerUnits.KNOT.getTranslationKey()).getString();
		} else {
			formattedSpeed += " " + Text.translatable(speedometer.units.getValue().getTranslationKey()).getString();
		}
		return formattedSpeed;
	}

	public static double getSpeed() {
		return speed;
	}

	public static String getFormattedSpeed() {
		return formattedSpeed;
	}
}
