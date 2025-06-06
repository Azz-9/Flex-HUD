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

public class HueWidget extends ClickableWidget {
	private float selectedHue;
	private double cursorY;

	private boolean isDraggingCursor = false;

	private final ColorUpdatable colorSelector;

	HueWidget(int width, int height, ColorUpdatable colorSelector) {
		super(0, 0, width, height, Text.translatable("better_hud.hue_bar"));
		this.colorSelector = colorSelector;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		drawHueBar(context);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getX(), getY() + cursorY, 0);

		// Draw the cursor
		int cursorWidth = getWidth();
		int cursorHeight = cursorWidth / 4;
		context.drawTexture(RenderLayer::getGuiTexturedOverlay, Identifier.of(MOD_ID, "widgets/color_selector/hue_cursor.png"),
				0, -2, 0, 0, cursorWidth, cursorHeight, cursorWidth, cursorHeight);

		matrices.pop();
	}

	private void drawHueBar(DrawContext context) {
		for (int i = 0; i < getHeight(); i++) {
			int color = Color.HSBtoRGB(i / (float) getHeight(), 1.0f, 1.0f);
			context.fill(getX(), getY() + i, getX() + getWidth(), getY() + i + 1, color);
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		long window = MinecraftClient.getInstance().getWindow().getHandle();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
		moveCursor(mouseY);
		isDraggingCursor = true;
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		moveCursor(mouseY);
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		long window = MinecraftClient.getInstance().getWindow().getHandle();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		isDraggingCursor = false;
	}

	private void moveCursor(double mouseY) {
		cursorY = Math.clamp(mouseY, getY(), getBottom()) - getY();
		updateHue(cursorY);
		
		colorSelector.onUpdateColor(ColorSelector.ColorSelectorElement.HUE);
	}

	private void updateHue(double cursorY) {
		selectedHue = (float) ((cursorY) * 360.0f / getHeight());
	}

	public boolean isDraggingCursor() {
		return isDraggingCursor;
	}

	public float getSelectedHue() {
		return selectedHue;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {

	}
}
