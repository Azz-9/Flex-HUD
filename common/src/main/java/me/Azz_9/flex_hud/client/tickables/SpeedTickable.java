package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public class SpeedTickable implements Tickable {
	private static Vec3 previousPosition;
	private static double speedMeterPerTicks = 0.0;
	private static double horizontalSpeedMeterPerTicks = 0.0;

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
			double xSquared = currentVector.x * currentVector.x;
			double ySquared = currentVector.y * currentVector.y;
			double zSquared = currentVector.z * currentVector.z;
			speedMeterPerTicks = Math.sqrt(xSquared + ySquared + zSquared);
			horizontalSpeedMeterPerTicks = Math.sqrt(xSquared + zSquared);
		}
		previousPosition = currentPosition;
	}

	public static double getSpeedMeterPerTicks() {
		return speedMeterPerTicks;
	}

	public static double getHorizontalSpeedMeterPerTicks() {
		return horizontalSpeedMeterPerTicks;
	}
}
