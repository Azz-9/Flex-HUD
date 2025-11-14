package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class ReachTickable implements Tickable {
	private static double reach = 0.0;
	private static long lastHitTime = -1;

	static {
		TickRegistry.register(new ReachTickable());
	}

	public static void calculateReach(PlayerEntity playerAttacking, Entity entityAttacked) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (entityAttacked instanceof LivingEntity &&
				client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY &&
				client.getCameraEntity() != null) {

			Vec3d lerpedPos = client.getCameraEntity().getLerpedPos(0);
			float eyeHeight = client.getCameraEntity().getEyeHeight(playerAttacking.getPose());
			Vec3d cameraPos = new Vec3d(lerpedPos.getX(), lerpedPos.getY() + eyeHeight, lerpedPos.getZ());

			reach = client.crosshairTarget.getPos().distanceTo(cameraPos);

			lastHitTime = System.currentTimeMillis();
		}
	}

	@Override
	public void tick(MinecraftClient client) {
		if (lastHitTime == -1 || System.currentTimeMillis() - lastHitTime > 5000) {
			reach = 0.0; // reset the reach 5s after the last hit
		}
	}

	public static double getReach() {
		return reach;
	}
}
