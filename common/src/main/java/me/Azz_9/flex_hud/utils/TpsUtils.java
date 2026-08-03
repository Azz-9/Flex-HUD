package me.Azz_9.flex_hud.utils;

public class TpsUtils {
	private static final int SAMPLE_COUNT = 5;

	private static final double[] msptSamples = new double[SAMPLE_COUNT];
	private static int index;
	private static int samples;
	private static double totalMspt;
	private static long lastGameTime = -1;
	private static long lastUpdateTime = -1;
	private static double averageTps = 20.0;

	public static void onServerTick(long gameTime) {
		long now = System.nanoTime();

		if (lastGameTime != -1) {
			long passedTicks = gameTime - lastGameTime;

			if (passedTicks > 0) {
				double elapsedMs = (now - lastUpdateTime) / 1_000_000.0;
				double mspt = elapsedMs / passedTicks;

				if (samples == SAMPLE_COUNT) {
					totalMspt -= msptSamples[index];
				} else {
					samples++;
				}

				msptSamples[index] = mspt;
				totalMspt += mspt;

				index = (index + 1) % SAMPLE_COUNT;

				double averageMspt = totalMspt / samples;
				averageTps = Math.min(20.0, 1000.0 / averageMspt);
			}
		}

		lastGameTime = gameTime;
		lastUpdateTime = now;
	}

	public static double getAverageTps() {
		return averageTps;
	}

	public static void reset() {
		index = 0;
		samples = 0;
		totalMspt = 0.0;
		lastGameTime = -1;
		lastUpdateTime = -1;
		averageTps = 20.0;
	}
}
