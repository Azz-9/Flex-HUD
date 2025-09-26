package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector;

import me.Azz_9.flex_hud.client.utils.Cursors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MOD_ID;

public class GradientWidget extends ClickableWidget {
	private float selectedHue;
	private int selectedColor;
	private double cursorX;
	private double cursorY;

	private boolean isDraggingCursor = false;

	private ColorUpdatable colorSelector;

	GradientWidget(int width, int height, ColorUpdatable colorSelector) {
		super(0, 0, width, height, Text.translatable("flex_hud.gradient_widget"));
		selectedHue = 0;
		selectedColor = 0;
		this.colorSelector = colorSelector;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.isInteractable() && this.isHovered()) {
			context.setCursor(Cursors.CROSSHAIR);
		}

		renderGradient(context);

		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate((float) (cursorX + getX()), (float) (cursorY + getY()));

		// Draw the cursor
		int cursorSize = 6;
		context.drawTexture(
				RenderPipelines.GUI_TEXTURED,
				Identifier.of(MOD_ID, "widgets/color_selector/gradient_cursor.png"),
				-cursorSize / 2, -cursorSize / 2,
				0, 0,
				cursorSize, cursorSize,
				cursorSize, cursorSize
		);

		matrices.popMatrix();
	}

	private void renderGradient(DrawContext context) {
		for (int x = 0; x < getWidth(); x++) {
			float saturation = x / (float) getWidth();

			int topColor = Color.HSBtoRGB(selectedHue / 360.0f, saturation, 1.0f);
			int bottomColor = Color.HSBtoRGB(selectedHue / 360.0f, saturation, 0.0f);
			context.fillGradient(getX() + x, getY(), getX() + x + 1, getBottom(), topColor, bottomColor);
		}
	}

	@Override
	public void onClick(Click click, boolean bl) {
		long window = MinecraftClient.getInstance().getWindow().getHandle();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
		moveCursor(click.x(), click.y());
		isDraggingCursor = true;
	}

	@Override
	public boolean mouseDragged(Click click, double offsetX, double offsetY) {
		if (isDraggingCursor) {
			return super.mouseDragged(click, offsetX, offsetY);
		}
		return false;
	}

	@Override
	protected void onDrag(Click click, double d, double e) {
		moveCursor(click.x(), click.y());
	}

	@Override
	public boolean mouseReleased(Click click) {
		if (isDraggingCursor) {
			return super.mouseReleased(click);
		}
		return false;
	}

	@Override
	public void onRelease(Click click) {
		long window = MinecraftClient.getInstance().getWindow().getHandle();
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
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}
}
