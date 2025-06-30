package me.Azz_9.better_hud.client.utils.cps;

import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.better_hud.client.configurableModules.modules.hud.custom.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
	private static boolean isAttackKeyPressed = false;
	private static boolean isUseKeyPressed = false;

	public static void onKey(int button, int action) {
		//TODO enlever les trucs de calcules de temps
		if (button == GLFW.GLFW_KEY_O && action == GLFW.GLFW_PRESS) {
			System.out.println("ArmorStatus: " + (ArmorStatus.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + ArmorStatus.times.size() + " times, average: " + (ArmorStatus.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("Clock: " + (Clock.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + Clock.times.size() + " times, average: " + (Clock.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("Coordinates: " + (Coordinates.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + Coordinates.times.size() + " times, average: " + (Coordinates.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("Cps: " + (Cps.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + Cps.times.size() + " times, average: " + (Cps.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("Direction: " + (Compass.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + Compass.times.size() + " times, average: " + (Compass.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("Fps: " + (Fps.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + Fps.times.size() + " times, average: " + (Fps.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("DayCounter: " + (DayCounter.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + DayCounter.times.size() + " times, average: " + (DayCounter.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("MemoryUsage: " + (MemoryUsage.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + MemoryUsage.times.size() + " times, average: " + (MemoryUsage.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("Ping: " + (Ping.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + Ping.times.size() + " times, average: " + (Ping.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("Playtime: " + (Playtime.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + Playtime.times.size() + " times, average: " + (Playtime.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("Reach: " + (Reach.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + Reach.times.size() + " times, average: " + (Reach.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("ServerAddress: " + (ServerAddress.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + ServerAddress.times.size() + " times, average: " + (ServerAddress.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
			System.out.println("Speedometer: " + (Speedometer.times.stream().mapToDouble(Long::doubleValue).sum() / 1_000_000) + "ms for " + Speedometer.times.size() + " times, average: " + (Speedometer.times.stream().mapToDouble(Long::doubleValue).average().orElse(0) / 1_000_000) + "ms");
		}

		if (!JsonConfigHelper.getInstance().isEnabled || !JsonConfigHelper.getInstance().cps.isEnabled()) {
			isAttackKeyPressed = false;
			isUseKeyPressed = false;
			return;
		}

		int attackKeyCode = KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.attackKey).getCode();
		int useKeyCode = KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.useKey).getCode();

		if ((button != attackKeyCode && button != useKeyCode) || (button == attackKeyCode && !JsonConfigHelper.getInstance().cps.showLeftClick.getValue()) || (button == useKeyCode && !JsonConfigHelper.getInstance().cps.showRightClick.getValue())) {
			isAttackKeyPressed = false;
			isUseKeyPressed = false;
			return;
		}
		if (action == GLFW.GLFW_PRESS) {
			if (!isAttackKeyPressed && button == attackKeyCode) {
				isAttackKeyPressed = true;
				CpsUtils.onAttackKeyPress();
				return;

			} else if (!isUseKeyPressed && button == useKeyCode) {
				isUseKeyPressed = true;
				CpsUtils.onUseKeyPress();
				return;
			}
		}
		isAttackKeyPressed = false;
		isUseKeyPressed = false;
	}
}
