package me.Azz_9.flex_hud.client.utils.memoryUsage;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

public class MemoryUsageUtils {
	private static int memoryUsage;

	public static void updateMemoryUsage() {
		// Accéder au gestionnaire de mémoire de la JVM
		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

		// Obtenir les informations sur la mémoire heap
		java.lang.management.MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

		// Mémoire utilisée et maximum allouée
		long usedMemory = heapMemoryUsage.getUsed();
		long maxMemory = heapMemoryUsage.getMax();

		// Calculer le pourcentage
		memoryUsage = (int) ((double) usedMemory / maxMemory * 100);
	}

	public static int getMemoryUsage() {
		return memoryUsage;
	}
}
