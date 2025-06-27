package me.Azz_9.better_hud.client.utils.reach;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class ReachUtils {
	private static double reach = 0.0;
	private static long lastHitTime = -1;

	public static void calculateReach(PlayerEntity playerAttacking, Entity entityAttacked) {

		Vec3d cameraPos = playerAttacking.getEyePos();
		Vec3d lookVec = playerAttacking.getRotationVec(1.0F);

		Box entityAttackedBox = entityAttacked.getBoundingBox();

		//length of the ray which represents the max distance from the player eyes and the entity
		double maxDistance = 0.0;
		for (double x : new double[]{entityAttackedBox.minX, entityAttackedBox.maxX}) {
			for (double y : new double[]{entityAttackedBox.minY, entityAttackedBox.maxY}) {
				for (double z : new double[]{entityAttackedBox.minZ, entityAttackedBox.maxZ}) {
					Vec3d corner = new Vec3d(x, y, z);
					double distance = cameraPos.distanceTo(corner);
					if (distance > maxDistance) {
						maxDistance = distance;
					}
				}
			}
		}
		Vec3d rayEnd = cameraPos.add(lookVec.multiply(maxDistance));

		Optional<Vec3d> hit = entityAttackedBox.raycast(cameraPos, rayEnd);

		reach = hit.map(cameraPos::distanceTo).orElseGet(() -> cameraPos.distanceTo(entityAttacked.getPos()));

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
