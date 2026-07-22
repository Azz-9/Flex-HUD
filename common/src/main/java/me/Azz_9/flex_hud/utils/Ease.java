package me.Azz_9.flex_hud.utils;

import net.minecraft.util.Mth;

public class Ease {

	public static float outQuad(final float x) {
		return 1.0F - Mth.square(1.0F - x);
	}
}
