package me.Azz_9.flex_hud.client.utils;

import net.minecraft.client.gui.GuiGraphics;

public class DrawingUtils {

	public static void drawBorder(GuiGraphics graphics, int x, int y, int width, int height, int color) {
		drawBorder(graphics, x, y, width, height, 1, color);
	}

	public static void drawBorder(GuiGraphics graphics, int x, int y, int width, int height, int thick, int color) {
		graphics.fill(x, y, x + width, y + thick, color);
		graphics.fill(x, y + height - thick, x + width, y + height, color);
		graphics.fill(x, y + thick, x + thick, y + height - thick, color);
		graphics.fill(x + width - thick, y + thick, x + width, y + height - thick, color);
	}
}
