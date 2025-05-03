package me.Azz_9.better_hud.client.utils;

import net.minecraft.block.entity.SculkShriekerWarningManager;
import net.minecraft.server.network.ServerPlayerEntity;

public class ShriekerWarningLevelUtils {
	private static int level;

	public static int getLevel() {
		return level;
	}

	public static void updateLevel(ServerPlayerEntity serverPlayer) {
		if (serverPlayer.getSculkShriekerWarningManager().isPresent()) {
			SculkShriekerWarningManager manager = serverPlayer.getSculkShriekerWarningManager().get();

			level = manager.getWarningLevel();
		}
	}
}
