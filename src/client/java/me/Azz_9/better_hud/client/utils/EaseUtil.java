package me.Azz_9.better_hud.client.utils;

public class EaseUtil {
	public static float getEaseOutQuad(float progress) {
		return 1 - (1 - progress) * (1 - progress);
	}

	public static float getEaseInCubic(float progress) {
		return progress * progress * progress;
	}

	public static float getEaseOutQart(float progress) {
		return (float) (1 - Math.pow(1 - progress, 4));
	}
}
