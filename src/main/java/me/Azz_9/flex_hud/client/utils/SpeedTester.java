package me.Azz_9.flex_hud.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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

		MinecraftClient client = MinecraftClient.getInstance();

		boolean keyPressed = InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_O);

		if (keyPressed && !wasKeyPressed) {
			for (String module : times.keySet()) {
				List<Long> moduleTimes = times.get(module);
				if (moduleTimes.isEmpty()) continue;

				// Compute average time in microseconds for readability
				double average = moduleTimes.stream().mapToLong(Long::longValue).average().orElse(0.0) / 1000.0;

				Text text = Text.literal(String.format("%s: avg ", module))
						.append(Text.literal(String.format("%.2fµs", average)).formatted(Formatting.AQUA))
						.append(", ")
						.append(Text.literal(String.format("%.2fms", average / 1000.0)).formatted(Formatting.RED))
						.append(String.format(" (%d samples ~%.2fs)", moduleTimes.size(), (float) (moduleTimes.size()) / MinecraftClient.getInstance().getCurrentFps()));

				if (client.player != null) {
					client.player.sendMessage(text, false);
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
