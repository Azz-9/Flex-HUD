package me.Azz_9.flex_hud.client.tickables;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;

public class RaycastTickable implements Tickable {

	private static HitResult hitResult;

	static {
		TickRegistry.register(new RaycastTickable());
	}

	@Override
	public boolean shouldTick() {
		return ModulesHelper.getInstance().signReader.enabled.getValue() || ModulesHelper.getInstance().distance.enabled.getValue();
	}

	public void tick(MinecraftClient client) {
		if (client.getCameraEntity() == null) return;

		int viewDistanceBlocks = client.options.getViewDistance().getValue() * 16;
		hitResult = client.getCameraEntity().raycast(viewDistanceBlocks, 0, false);
	}

	public static HitResult getHitResult() {
		return hitResult;
	}
}
