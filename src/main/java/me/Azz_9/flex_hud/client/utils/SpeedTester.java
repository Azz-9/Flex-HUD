package me.Azz_9.flex_hud.client.utils;


import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpeedTester {
	private static Map<String, List<Long>> times = new HashMap<>();
	private static Map<String, Long> starts = new HashMap<>();
	private static boolean wasKeyPressed = false;
	private static final int MAX_SAMPLES = 200;

	// Returns the list of recorded times for the given module
	public static List<Long> getTimes(String module) {
		times.computeIfAbsent(module, key -> new ArrayList<>());
		return times.get(module);
	}

	// Starts the timer for the given module
	public static void start(String module) {
		starts.put(module, System.nanoTime());
	}


	// Ends the timer and records the elapsed time
	public static void end(String module) {
		if (starts.containsKey(module)) {
			long elapsed = System.nanoTime() - starts.get(module);
			List<Long> list = getTimes(module);
			list.add(elapsed);
			if (list.size() > MAX_SAMPLES) {
				list.removeFirst(); // Remove oldest sample
			}
			starts.remove(module);
		}
	}

	// Called every tick — prints average render time when pressing O
	public static void tick() {
		if (times.isEmpty()) return;

		Minecraft minecraft = Minecraft.getInstance();

		boolean keyPressed = InputConstants.isKeyDown(minecraft.getWindow(), InputConstants.KEY_O);

		if (keyPressed && !wasKeyPressed) {
			for (String module : times.keySet()) {
				List<Long> moduleTimes = times.get(module);
				if (moduleTimes.isEmpty()) continue;

				// Compute average time in microseconds for readability
				double average = moduleTimes.stream().mapToLong(Long::longValue).average().orElse(0.0) / 1000.0;

				Component text = Component.literal(String.format("%s: avg ", module))
						.append(Component.literal(String.format("%.2fµs", average)).withStyle(ChatFormatting.AQUA))
						.append(", ")
						.append(Component.literal(String.format("%.2fms", average / 1000.0)).withStyle(ChatFormatting.RED))
						.append(String.format(" (%d samples ~%.2fs)", moduleTimes.size(), (float) (moduleTimes.size()) / Minecraft.getInstance().getFps()));

				if (minecraft.player != null) {
					minecraft.player.displayClientMessage(text, false);
				}
				System.out.println(text.getString());
			}
			System.out.println();
			wasKeyPressed = true;
		} else if (!keyPressed) {
			wasKeyPressed = false;
		}
	}
}
