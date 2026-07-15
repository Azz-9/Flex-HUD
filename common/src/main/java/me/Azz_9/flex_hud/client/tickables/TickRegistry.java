package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.Minecraft;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TickRegistry {
	private static final @NotNull List<@NotNull Tickable> TICKABLES = new ArrayList<>();

	public static void register(@NotNull Tickable tickable) {
		TICKABLES.add(tickable);
	}

	public static void tickAll(@NotNull Minecraft minecraft) {
		for (Tickable tickable : TICKABLES) {
			if (tickable.shouldTick())
				tickable.tick(minecraft);
		}
	}
}