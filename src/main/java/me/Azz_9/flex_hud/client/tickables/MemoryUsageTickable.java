package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.MinecraftClient;

public class MemoryUsageTickable implements Tickable {
	private static final int INTERVAL = 500;

	private static double usedMemoryPercentage = 0;
	private static long usedMemory = 0;
	private static long maxMemory = 0;

	static {
		TickRegistry.register(new MemoryUsageTickable());
	}

	@Override
	public void tick(MinecraftClient client) {
		Runtime runtime = Runtime.getRuntime();
		long maxMem = runtime.maxMemory();
		long totalMem = runtime.totalMemory();
		long freeMem = runtime.freeMemory();

		usedMemory = totalMem - freeMem;
		maxMemory = maxMem;
		usedMemoryPercentage = (double) usedMemory / maxMemory * 100;
	}

	public static double getUsedMemoryPercentage() {
		return usedMemoryPercentage;
	}

	public static long getUsedMemory() {
		return usedMemory;
	}

	public static long getMaxMemory() {
		return maxMemory;
	}
}
