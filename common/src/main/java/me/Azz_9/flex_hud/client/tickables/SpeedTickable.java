package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.phys.Vec3;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.hud.custom.Speedometer;

public class SpeedTickable implements Tickable {
	private static Vec3 previousPosition;
	private static double speed = 0.0;

	static {
		TickRegistry.register(new SpeedTickable());
	}

	@Override
	public void tick(Minecraft minecraft) {
		LocalPlayer player = minecraft.player;
		if (player == null) {
			return;
		}

		Vec3 currentPosition = player.getPosition(0);
		if (previousPosition != null) {
			Vec3 currentVector = new Vec3(
					currentPosition.x - previousPosition.x,
					currentPosition.y - previousPosition.y,
					currentPosition.z - previousPosition.z
			);
			speed = Math.sqrt(Math.pow(currentVector.x, 2) + Math.pow(currentVector.y, 2) + Math.pow(currentVector.z, 2)) * 20; // speed in blocks per seconds

			if (Modules.getInstance().speedometer.units.getValue() == Speedometer.SpeedometerUnits.KNOT || (Modules.getInstance().speedometer.useKnotInBoat.getValue() && player.getVehicle() instanceof Boat)) {
				speed = speed * 1.9438452492;
			} else if (Modules.getInstance().speedometer.units.getValue() == Speedometer.SpeedometerUnits.KPH) {
				speed = speed * 3.6;
			} else if (Modules.getInstance().speedometer.units.getValue() == Speedometer.SpeedometerUnits.MPH) {
				speed = speed * 2.2369362921;
			} // no need MPS because the speed is already in meters per seconds

		}
		previousPosition = currentPosition;
	}

	public static double getSpeed() {
		return speed;
	}
}
