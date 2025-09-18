package me.Azz_9.flex_hud.client.utils;

public class EaseUtils {
	public static float getEaseOutQuad(float progress) {
		return 1 - (1 - progress) * (1 - progress);
	}
}
