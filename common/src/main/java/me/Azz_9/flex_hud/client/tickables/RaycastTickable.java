package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;

import me.Azz_9.flex_hud.client.modules.Modules;

public class RaycastTickable implements Tickable {

	private static HitResult hitResult;

	static {
		TickRegistry.register(new RaycastTickable());
	}

	@Override
	public boolean shouldTick() {
		return Modules.getInstance().signReader.enabled.getValue() || Modules.getInstance().distance.enabled.getValue();
	}

	@Override
	public void tick(Minecraft minecraft) {
		if (minecraft.getCameraEntity() == null) return;

		int viewDistanceBlocks = minecraft.options.renderDistance().get() * 16;
		hitResult = minecraft.getCameraEntity().pick(viewDistanceBlocks, 0, false);
	}

	public static HitResult getHitResult() {
		return hitResult;
	}
}
