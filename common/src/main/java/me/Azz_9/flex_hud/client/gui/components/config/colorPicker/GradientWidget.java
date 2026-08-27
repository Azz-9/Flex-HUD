package me.Azz_9.flex_hud.client.gui.components.config.colorPicker;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.Constants.MOD_ID;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import me.Azz_9.flex_hud.utils.DrawingUtils;

public class GradientWidget extends AbstractWidget {

	private static final ResourceLocation CURSOR = ResourceLocation.fromNamespaceAndPath(MOD_ID, "widget/color_picker/gradient_cursor");

	private static final int CURSOR_SIZE = 6;

	private float selectedHue;
	private int selectedColor;
	private double cursorX;
	private double cursorY;

	private boolean isDraggingCursor = false;

	private final ColorUpdatable colorPicker;

	GradientWidget(int width, int height, ColorUpdatable colorPicker) {
		super(0, 0, width, height, Component.translatable("flex_hud.gradient_widget"));
		selectedHue = 0;
		selectedColor = 0;
		this.colorPicker = colorPicker;
	}

	@Override
	protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		DrawingUtils.drawColorPickerGradient(graphics, getX(), getY(), getWidth(), getHeight(), selectedHue);

		PoseStack matrices = graphics.pose();
		matrices.pushPose();
		matrices.translate((float) (cursorX + getX()), (float) (cursorY + getY()), 0);

		// Draw the cursor
		graphics.blitSprite(
				RenderType::guiTextured,
				CURSOR,
				-CURSOR_SIZE / 2, -CURSOR_SIZE / 2,
				CURSOR_SIZE, CURSOR_SIZE
		);

		matrices.popPose();
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		long window = MINECRAFT.getWindow().getWindow();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
		moveCursor(mouseX, mouseY);
		isDraggingCursor = true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (isDraggingCursor) {
			return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
		}
		return false;
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
		moveCursor(mouseX, mouseY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (isDraggingCursor) {
			return super.mouseReleased(mouseX, mouseY, button);
		}
		return false;
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		long window = MINECRAFT.getWindow().getWindow();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		isDraggingCursor = false;
	}

	private void moveCursor(double mouseX, double mouseY) {
		cursorX = Math.clamp(mouseX, getX(), getRight()) - getX();
		cursorY = Math.clamp(mouseY, getY(), getBottom()) - getY();
		updateColor(cursorX, cursorY);

		colorPicker.onUpdateColor(ColorPicker.ColorPickerElement.GRADIENT);
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
	protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
	}
}
