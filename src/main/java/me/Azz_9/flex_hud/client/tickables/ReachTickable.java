package me.Azz_9.flex_hud.client.tickables;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ReachTickable implements Tickable {
	private static double reach = 0.0;
	private static long lastHitTime = -1;

	static {
		TickRegistry.register(new ReachTickable());
	}

	public static void calculateReach(Player playerAttacking, Entity entityAttacked) {
		if (entityAttacked instanceof LivingEntity &&
				MINECRAFT.hitResult != null && MINECRAFT.hitResult.getType() == HitResult.Type.ENTITY &&
				MINECRAFT.getCameraEntity() != null) {

			Vec3 lerpedPos = MINECRAFT.getCameraEntity().getPosition(0);
			float eyeHeight = MINECRAFT.getCameraEntity().getEyeHeight(playerAttacking.getPose());
			Vec3 cameraPos = new Vec3(lerpedPos.x(), lerpedPos.y() + eyeHeight, lerpedPos.z());

			reach = MINECRAFT.hitResult.getLocation().distanceTo(cameraPos);

			lastHitTime = System.currentTimeMillis();
		}
	}

	@Override
	public void tick(Minecraft minecraft) {
		if (lastHitTime == -1 || System.currentTimeMillis() - lastHitTime > 5000) {
			reach = 0.0; // reset the reach 5s after the last hit
		}
	}

	public static double getReach() {
		return reach;
	}
}
