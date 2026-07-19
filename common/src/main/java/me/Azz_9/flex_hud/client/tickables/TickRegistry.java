package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.Minecraft;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.Azz_9.flex_hud.Constants;
import me.Azz_9.flex_hud.client.debug.PerfTester;

public class TickRegistry {
	private static final @NotNull List<@NotNull Tickable> tickables = new ArrayList<>();

	public static void register(@NotNull Tickable tickable) {
		tickables.add(tickable);
	}

	public static void tickAll(@NotNull Minecraft minecraft) {
		for (Tickable tickable : tickables) {
			if (tickable.shouldTick())
				if (Constants.DEBUG) {
					PerfTester.testTick(
							tickable.getClass().getSimpleName(),
							() -> tickable.tick(minecraft)
					);
				} else {
					tickable.tick(minecraft);
				}
		}
	}
}