package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector;

import me.Azz_9.flex_hud.client.utils.Cursors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class GradientWidget extends AbstractWidget.WithInactiveMessage {
	private float selectedHue;
	private int selectedColor;
	private double cursorX;
	private double cursorY;

	private boolean isDraggingCursor = false;

	private ColorUpdatable colorSelector;

	GradientWidget(int width, int height, ColorUpdatable colorSelector) {
		super(0, 0, width, height, Component.translatable("flex_hud.gradient_widget"));
		selectedHue = 0;
		selectedColor = 0;
		this.colorSelector = colorSelector;
	}

	@Override
	protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.isActive() && this.isHovered()) {
			graphics.requestCursor(Cursors.CROSSHAIR);
		}

		renderGradient(graphics);

		Matrix3x2fStack matrices = graphics.pose();
		matrices.pushMatrix();
		matrices.translate((float) (cursorX + getX()), (float) (cursorY + getY()));

		// Draw the cursor
		int cursorSize = 6;
		graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				Identifier.fromNamespaceAndPath(MOD_ID, "widgets/color_selector/gradient_cursor.png"),
				-cursorSize / 2, -cursorSize / 2,
				0, 0,
				cursorSize, cursorSize,
				cursorSize, cursorSize
		);

		matrices.popMatrix();
	}

	private void renderGradient(GuiGraphics graphics) {
		for (int x = 0; x < getWidth(); x++) {
			float saturation = x / (float) getWidth();

			int topColor = Color.HSBtoRGB(selectedHue / 360.0f, saturation, 1.0f);
			int bottomColor = Color.HSBtoRGB(selectedHue / 360.0f, saturation, 0.0f);
			graphics.fillGradient(getX() + x, getY(), getX() + x + 1, getBottom(), topColor, bottomColor);
		}
	}

	@Override
	public void onClick(MouseButtonEvent click, boolean bl) {
		long window = Minecraft.getInstance().getWindow().handle();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
		moveCursor(click.x(), click.y());
		isDraggingCursor = true;
	}

	@Override
	public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
		if (isDraggingCursor) {
			return super.mouseDragged(click, offsetX, offsetY);
		}
		return false;
	}

	@Override
	protected void onDrag(MouseButtonEvent click, double d, double e) {
		moveCursor(click.x(), click.y());
	}

	@Override
	public boolean mouseReleased(@NonNull MouseButtonEvent click) {
		if (isDraggingCursor) {
			return super.mouseReleased(click);
		}
		return false;
	}

	@Override
	public void onRelease(@NonNull MouseButtonEvent click) {
		long window = Minecraft.getInstance().getWindow().handle();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		isDraggingCursor = false;
	}

	private void moveCursor(double mouseX, double mouseY) {
		cursorX = Math.clamp(mouseX, getX(), getRight()) - getX();
		cursorY = Math.clamp(mouseY, getY(), getBottom()) - getY();
		updateColor(cursorX, cursorY);

		colorSelector.onUpdateColor(ColorSelector.ColorSelectorElement.GRADIENT);
	}

	private void updateColor(double cursorX, double cursorY) {
		float saturation = (float) cursorX / getWidth();
		float brightness = 1.0f - (float) cursorY / getHeight();

		selectedColor = Color.HSBtoRGB(selectedHue / 360.0f, saturation, brightness) & 0x00ffffff;
	}

	public void updateColor(int color) {
		selectedColor = color;
		float[] hsbValues = new float[3];
		Color.RGBtoHSB((selectedColor >> 16) & 0xFF, (selectedColor >> 8) & 0xFF, selectedColor & 0xFF, hsbValues);
		selectedHue = hsbValues[0] * 360.0f;
		setCursorPositionToSelectedColor();
	}

	void updateHue(float hue) {
		selectedHue = hue;
		updateColor(cursorX, cursorY);
	}

	public void setCursorPositionToSelectedColor() {
		float[] hsbValues = new float[3];
		Color.RGBtoHSB((selectedColor >> 16) & 0xFF, (selectedColor >> 8) & 0xFF, selectedColor & 0xFF, hsbValues);
		float saturation = hsbValues[1];
		float brightness = hsbValues[2];

		cursorX = saturation * getWidth();
		cursorY = (1.0f - brightness) * getHeight();
	}

	public boolean isDraggingCursor() {
		return isDraggingCursor;
	}

	public int getSelectedColor() {
		return selectedColor;
	}

	@Override
	protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
	}
}
