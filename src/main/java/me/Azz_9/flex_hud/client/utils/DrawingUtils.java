package me.Azz_9.flex_hud.client.utils;

import net.minecraft.client.gui.DrawContext;

public class DrawingUtils {

	public static void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
		drawBorder(context, x, y, width, height, 1, color);
	}

	public static void drawBorder(DrawContext context, int x, int y, int width, int height, int thick, int color) {
		context.fill(x, y, x + width, y + thick, color);
		context.fill(x, y + height - thick, x + width, y + height, color);
		context.fill(x, y + thick, x + thick, y + height - thick, color);
		context.fill(x + width - thick, y + thick, x + width, y + height - thick, color);
	}
}
