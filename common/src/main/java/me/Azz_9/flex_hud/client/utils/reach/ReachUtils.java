package me.Azz_9.flex_hud.client.utils.reach;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class ReachUtils {
	private static double reach = 0.0;
	private static long lastHitTime = -1;

	public static void calculateReach(PlayerEntity playerAttacking, Entity entityAttacked) {
		if (entityAttacked instanceof LivingEntity) {
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

			if (hit.isPresent()) {
				reach = hit.map(cameraPos::distanceTo).get();
			}

			lastHitTime = System.currentTimeMillis();
		}
	}

	private static Vec3d compareTo(Vec3d compare, Vec3d test, AtomicDouble max) {
		double dist = compare.distanceTo(test);
		if (dist > max.get()) {
			max.set(dist);
			return test;
		}
		return compare;
	}

	public static void tick() {
		if (lastHitTime == -1 || System.currentTimeMillis() - lastHitTime > 5000) {
			reach = 0.0; // reset the reach 5s after the last hit
		}
	}

	public static double getReach() {
		return reach;
	}
}
