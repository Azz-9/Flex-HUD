package me.Azz_9.flex_hud.client.tickables;

import net.minecraft.client.MinecraftClient;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MemoryUsageTickable implements Tickable {
	private static double usedMemoryPercentage = 0;
	private static long usedMemory = 0;
	private static long maxMemory = 0;

	static {
		TickRegistry.register(new MemoryUsageTickable());
	}

	@Override
	public void tick(MinecraftClient client) {
		// Accéder au gestionnaire de mémoire de la JVM
		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

		// Obtenir les informations sur la mémoire heap
		MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

		// Mémoire utilisée et maximum allouée
		usedMemory = heapMemoryUsage.getUsed();
		maxMemory = heapMemoryUsage.getMax();

		// Calculer le pourcentage
		usedMemoryPercentage = ((double) usedMemory / maxMemory * 100);
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
