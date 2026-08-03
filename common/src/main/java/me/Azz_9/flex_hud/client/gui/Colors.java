package me.Azz_9.flex_hud.client.gui;

import net.minecraft.util.ARGB;

public final class Colors {

	public static final int WHITE = 0xFFFFFFFF;
	public static final int BLACK = 0xFF000000;
	public static final int BLACK_SEMI_TRANSPARENT = ARGB.color(0x7F, BLACK);
	public static final int BLACK_TRANSPARENT = ARGB.color(0xA0, BLACK);
	public static final int LIGHT_GRAY = 0xFF9C9C9C;
	public static final int GRAY = 0xFF4E4E4E;
	public static final int DARK_GRAY = 0xFF333333;
	public static final int RED = 0xFFFF5555;

	private Colors() {
	}

	public static int setBrightness(final int color, final float brightness) {
		int red = ARGB.red(color);
		int green = ARGB.green(color);
		int blue = ARGB.blue(color);
		int alpha = ARGB.alpha(color);
		int rgbMax = Math.max(Math.max(red, green), blue);
		int rgbMin = Math.min(Math.min(red, green), blue);
		float rgbConstantRange = rgbMax - rgbMin;
		float saturation;
		if (rgbMax != 0) {
			saturation = rgbConstantRange / rgbMax;
		} else {
			saturation = 0.0F;
		}

		float hue;
		if (saturation == 0.0F) {
			hue = 0.0F;
		} else {
			float constantRed = (rgbMax - red) / rgbConstantRange;
			float constantGreen = (rgbMax - green) / rgbConstantRange;
			float constantBlue = (rgbMax - blue) / rgbConstantRange;
			if (red == rgbMax) {
				hue = constantBlue - constantGreen;
			} else if (green == rgbMax) {
				hue = 2.0F + constantRed - constantBlue;
			} else {
				hue = 4.0F + constantGreen - constantRed;
			}

			hue /= 6.0F;
			if (hue < 0.0F) {
				hue++;
			}
		}

		if (saturation == 0.0F) {
			red = green = blue = Math.round(brightness * 255.0F);
			return ARGB.color(alpha, red, green, blue);
		}

		float colorWheelSegment = (hue - (float) Math.floor(hue)) * 6.0F;
		float colorWheelOffset = colorWheelSegment - (float) Math.floor(colorWheelSegment);
		float primaryColor = brightness * (1.0F - saturation);
		float secondaryColor = brightness * (1.0F - saturation * colorWheelOffset);
		float tertiaryColor = brightness * (1.0F - saturation * (1.0F - colorWheelOffset));
		switch ((int) colorWheelSegment) {
			case 0:
				red = Math.round(brightness * 255.0F);
				green = Math.round(tertiaryColor * 255.0F);
				blue = Math.round(primaryColor * 255.0F);
				break;
			case 1:
				red = Math.round(secondaryColor * 255.0F);
				green = Math.round(brightness * 255.0F);
				blue = Math.round(primaryColor * 255.0F);
				break;
			case 2:
				red = Math.round(primaryColor * 255.0F);
				green = Math.round(brightness * 255.0F);
				blue = Math.round(tertiaryColor * 255.0F);
				break;
			case 3:
				red = Math.round(primaryColor * 255.0F);
				green = Math.round(secondaryColor * 255.0F);
				blue = Math.round(brightness * 255.0F);
				break;
			case 4:
				red = Math.round(tertiaryColor * 255.0F);
				green = Math.round(primaryColor * 255.0F);
				blue = Math.round(brightness * 255.0F);
				break;
			case 5:
				red = Math.round(brightness * 255.0F);
				green = Math.round(primaryColor * 255.0F);
				blue = Math.round(secondaryColor * 255.0F);
		}

		return ARGB.color(alpha, red, green, blue);
	}
}