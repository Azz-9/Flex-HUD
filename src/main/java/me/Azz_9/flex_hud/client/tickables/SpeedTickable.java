package me.Azz_9.flex_hud.client.tickables;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.Speedometer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;

public class SpeedTickable implements Tickable {
	private static Vec3d previousPosition = null;
	private static double speed = 0.0;

	static {
		TickRegistry.register(new SpeedTickable());
	}

	@Override
	public void tick(MinecraftClient client) {
		PlayerEntity player = client.player;
		if (player == null) {
			return;
		}

		Vec3d currentPosition = player.getPos();
		if (previousPosition != null) {
			Vec3d currentVector = new Vec3d(
					currentPosition.x - previousPosition.x,
					currentPosition.y - previousPosition.y,
					currentPosition.z - previousPosition.z
			);
			speed = Math.sqrt(Math.pow(currentVector.x, 2) + Math.pow(currentVector.y, 2) + Math.pow(currentVector.z, 2)) * 20; // speed in blocks per seconds

			if (ModulesHelper.getInstance().speedometer.units.getValue() == Speedometer.SpeedometerUnits.KNOT || (ModulesHelper.getInstance().speedometer.useKnotInBoat.getValue() && player.getVehicle() instanceof BoatEntity)) {
				speed = speed * 1.9438452492;
			} else if (ModulesHelper.getInstance().speedometer.units.getValue() == Speedometer.SpeedometerUnits.KPH) {
				speed = speed * 3.6;
			} else if (ModulesHelper.getInstance().speedometer.units.getValue() == Speedometer.SpeedometerUnits.MPH) {
				speed = speed * 2.2369362921;
			} // no need MPS because the speed is already in meters per seconds

		}
		previousPosition = currentPosition;
	}

	public static double getSpeed() {
		return speed;
	}
}
