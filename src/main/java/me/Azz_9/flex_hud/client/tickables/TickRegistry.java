package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class TickRegistry {
	private static final List<Tickable> TICKABLES = new ArrayList<>();

	public static void register(Tickable tickable) {
		TICKABLES.add(tickable);
	}

	public static void tickAll(MinecraftClient client) {
		for (Tickable tickable : TICKABLES) {
			if (tickable.shouldTick()) tickable.tick(client);
		}
	}
}
