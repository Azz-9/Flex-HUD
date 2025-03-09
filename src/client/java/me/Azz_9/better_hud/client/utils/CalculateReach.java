package me.Azz_9.better_hud.client.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class CalculateReach {
	private static double reach = 0.0;
	private static long lastHitTime = -1;

	public static void calculateReach(PlayerEntity playerAttacking, Entity entityAttacked) {

		Vec3d posAttacking = playerAttacking.getPos();
		Vec3d posAttacked = entityAttacked.getPos();

		reach = posAttacking.distanceTo(posAttacked);

		lastHitTime = System.currentTimeMillis();
	}

	public static double getReach() {
		return reach;
	}

	public static long getLastHitTime() {
		return lastHitTime;
	}

	public static void resetReach() {
		reach = 0.0;
	}
}
