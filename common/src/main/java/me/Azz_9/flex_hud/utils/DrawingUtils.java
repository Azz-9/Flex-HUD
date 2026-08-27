package me.Azz_9.flex_hud.utils;

import static me.Azz_9.flex_hud.client.gui.FlexHudRenderTypes.COLOR_PICKER_GRADIENT;
import static me.Azz_9.flex_hud.client.gui.FlexHudRenderTypes.COLOR_PICKER_HUE;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import org.joml.Matrix4f;

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

	public static void drawColorPickerGradient(GuiGraphics graphics, int x, int y, int width, int height, float hue) {
		Matrix4f pose = graphics.pose().last().pose();

		VertexConsumer consumer = graphics.bufferSource.getBuffer(COLOR_PICKER_GRADIENT);

		addGradientVertices(
				consumer,
				pose,
				x, y,
				x + width,
				y + height,
				0,
				hue / 360.0f
		);
	}

	public static void drawColorPickerHueBar(GuiGraphics graphics, int x, int y, int width, int height) {
		Matrix4f pose = graphics.pose().last().pose();

		VertexConsumer consumer = graphics.bufferSource.getBuffer(COLOR_PICKER_HUE);

		addHueVertices(
				consumer,
				pose,
				x, y,
				x + width,
				y + height,
				0
		);
	}

	private static void addGradientVertices(
			VertexConsumer consumer,
			Matrix4f pose,
			int x0, int y0,
			int x1, int y1,
			int z,
			float hue
	) {
		int h = Mth.clamp(
				Mth.floor(hue * 255.0f),
				0,
				255
		);

		consumer.addVertex(pose, x0, y0, z)
				.setColor(h, 0, 255, 255);

		consumer.addVertex(pose, x0, y1, z)
				.setColor(h, 0, 0, 255);

		consumer.addVertex(pose, x1, y1, z)
				.setColor(h, 255, 0, 255);

		consumer.addVertex(pose, x1, y0, z)
				.setColor(h, 255, 255, 255);
	}

	private static void addHueVertices(
			VertexConsumer consumer,
			Matrix4f pose,
			int x0, int y0,
			int x1, int y1,
			int z
	) {
		consumer.addVertex(pose, x0, y0, z)
				.setColor(0, 255, 255, 255);

		consumer.addVertex(pose, x0, y1, z)
				.setColor(255, 255, 255, 255);

		consumer.addVertex(pose, x1, y1, z)
				.setColor(255, 255, 255, 255);

		consumer.addVertex(pose, x1, y0, z)
				.setColor(0, 255, 255, 255);
	}
}
