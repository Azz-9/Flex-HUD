package me.Azz_9.better_hud.client.screens.widgets.buttons.configButtons.colorSelector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class GradientWidget extends ClickableWidget {
	private float selectedHue;
	private int selectedColor;
	private double cursorX;
	private double cursorY;

	private boolean isDraggingCursor = false;

	private ColorUpdatable colorSelector;

	GradientWidget(int width, int height, ColorUpdatable colorSelector) {
		super(0, 0, width, height, Text.translatable("better_hud.gradient_widget"));
		selectedHue = 0;
		selectedColor = 0;
		this.colorSelector = colorSelector;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		renderGradient(context);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(cursorX + getX(), cursorY + getY(), 0);

		// Draw the cursor
		int cursorSize = 6;
		context.drawTexture(
				RenderLayer::getGuiTexturedOverlay,
				Identifier.of(MOD_ID, "widgets/color_selector/gradient_cursor.png"),
				-cursorSize / 2, -cursorSize / 2,
				0, 0,
				cursorSize, cursorSize,
				cursorSize, cursorSize
		);

		matrices.pop();
	}

	private void renderGradient(DrawContext context) {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				float saturation = x / (float) getWidth();
				float brightness = 1.0f - y / (float) getHeight();
				int color = Color.HSBtoRGB(selectedHue / 360.0f, saturation, brightness);
				context.fill(getX() + x, getY() + y, getX() + x + 1, getY() + y + 1, color);
			}
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		long window = MinecraftClient.getInstance().getWindow().getHandle();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
		moveCursor(mouseX, mouseY);
		isDraggingCursor = true;
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		moveCursor(mouseX, mouseY);
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
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

	void updateHue(float hue) {
		selectedHue = hue;
		updateColor(cursorX, cursorY);
	}

	public boolean isDraggingCursor() {
		return isDraggingCursor;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}
}
