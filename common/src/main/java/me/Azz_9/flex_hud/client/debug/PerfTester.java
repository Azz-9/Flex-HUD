package me.Azz_9.flex_hud.client.debug;


import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.util.*;

import me.Azz_9.flex_hud.FlexHudLogger;

public class PerfTester {
	private static final Map<String, List<Long>> frameTimes = new HashMap<>();
	private static final Map<String, List<Long>> tickTimes = new HashMap<>();
	private static final Map<String, Long> frameStarts = new HashMap<>();
	private static final Map<String, Long> tickStarts = new HashMap<>();
	private static final int MAX_FRAME_SAMPLES = 200;
	private static final int MAX_TICK_SAMPLES = 600;

	private static final int MAX_FRAME_TIME = 50;
	private static final int MAX_TICK_TIME = 150;

	private static final double FRAME_BUDGET_NS = 1_000_000_000.0 / 60.0;
	private static final double TICK_BUDGET_NS = 50_000_000.0;

	private static List<String> getModules() {
		List<String> modules = new ArrayList<>(frameTimes.keySet());
		modules.addAll(tickTimes.keySet());
		return modules;
	}

	// start
	private static void start(String module, Map<String, Long> starts) {
		starts.put(module, System.nanoTime());
	}

	public static void startFrame(String module) {
		start(module, frameStarts);
	}

	public static void startTick(String module) {
		start(module, tickStarts);
	}

	// end
	public static void end(String module, Map<String, Long> starts, Map<String, List<Long>> times, int maxSamples) {
		if (starts.containsKey(module)) {
			long elapsed = System.nanoTime() - starts.get(module);
			times.computeIfAbsent(module, _ -> new ArrayList<>());
			List<Long> list = times.get(module);
			list.add(elapsed);
			if (list.size() > maxSamples) {
				list.removeFirst(); // Remove oldest sample
			}
			starts.remove(module);
		}
	}

	public static void endFrame(String module) {
		end(module, frameStarts, frameTimes, MAX_FRAME_SAMPLES);
	}

	public static void endTick(String module) {
		end(module, tickStarts, tickTimes, MAX_TICK_SAMPLES);
	}

	public static void print() {
		List<PerfResult> frameResults = createResults(frameTimes, FRAME_BUDGET_NS, MAX_FRAME_TIME);
		List<PerfResult> tickResults = createResults(tickTimes, TICK_BUDGET_NS, MAX_TICK_TIME);

		if (!frameResults.isEmpty()) {
			printHeader("Frames");

			frameResults.stream()
					.sorted(Comparator.comparingDouble(PerfResult::averageNs))
					.forEach(PerfTester::print);
		}

		if (!tickResults.isEmpty()) {
			printHeader("Ticks");

			tickResults.stream()
					.sorted(Comparator.comparingDouble(PerfResult::averageNs))
					.forEach(PerfTester::print);
		}

		FlexHudLogger.debug("");
	}

	private static List<PerfResult> createResults(
			Map<String, List<Long>> timesByModule,
			double budgetNs,
			int maxTime
	) {
		List<PerfResult> results = new ArrayList<>();

		for (Map.Entry<String, List<Long>> entry : timesByModule.entrySet()) {
			List<Long> times = entry.getValue();

			if (times.isEmpty()) {
				continue;
			}

			double averageNs = times.stream()
					.mapToLong(Long::longValue)
					.average()
					.orElse(0.0);

			results.add(new PerfResult(entry.getKey(), maxTime, averageNs, budgetNs));
		}

		return results;
	}

	private static void printHeader(String name) {
		Component text = Component.literal("=== " + name + " ===");

		if (MINECRAFT.player != null) {
			MINECRAFT.player.sendSystemMessage(text);
		}

		FlexHudLogger.debug(text.getString());
	}

	private static void print(PerfResult result) {
		double averageUs = result.averageNs() / 1_000.0;
		double averageMs = result.averageNs() / 1_000_000.0;
		double percentage = result.averageNs() / result.budgetNs() * 100.0;

		Component text = Component.literal(String.format("%s: avg ", result.module()))
				.append(Component.literal(String.format("%.2fµs", averageUs)).withColor(getColor(averageUs, result.maxTime)))
				.append(", ")
				.append(Component.literal(String.format("%.4fms", averageMs)).withColor(getColor(averageUs, result.maxTime)))
				.append(" - ")
				.append(Component.literal(String.format("%.4f%%", percentage)).withColor(getColor(averageUs, result.maxTime)));

		if (MINECRAFT.player != null) {
			MINECRAFT.player.sendSystemMessage(text);
		}

		FlexHudLogger.debug(text.getString());
	}

	private static int getColor(double average, double max) {
		float alpha = (float) Mth.clamp(average / max, 0, 1);
		return ARGB.srgbLerp(alpha, TextColor.GREEN.getValue(), TextColor.RED.getValue());
	}

	private record PerfResult(
			String module,
			int maxTime,
			double averageNs,
			double budgetNs
	) {
	}
}
