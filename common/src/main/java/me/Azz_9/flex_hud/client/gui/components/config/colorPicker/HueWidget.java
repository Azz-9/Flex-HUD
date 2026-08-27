package me.Azz_9.flex_hud.client.gui.components.config.colorPicker;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;
import static me.Azz_9.flex_hud.Constants.MOD_ID;

import net.minecraft.client.gui.GuiGraphicsExtractor;
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

import me.Azz_9.flex_hud.client.gui.Cursors;

public class HueWidget extends AbstractWidget.WithInactiveMessage {

	private static final Identifier CURSOR = Identifier.fromNamespaceAndPath(MOD_ID, "widget/color_picker/hue_cursor");

	private float selectedHue;
	private double cursorY;

	private boolean isDraggingCursor = false;

	private final ColorUpdatable colorPicker;

	HueWidget(int width, int height, ColorUpdatable colorPicker) {
		super(0, 0, width, height, Component.translatable("flex_hud.hue_bar"));
		this.colorPicker = colorPicker;
	}

	@Override
	protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.isActive() && this.isHovered()) {
			graphics.requestCursor(Cursors.POINTING_HAND);
		}

		Matrix3x2fStack matrices = graphics.pose();
		graphics.guiRenderState.addGuiElement(
				new ColorPickerHueRenderState(
						matrices,
						getX(), getY(),
						getRight(), getBottom(),
						graphics.scissorStack.peek()
				)
		);

		matrices.pushMatrix();
		matrices.translate((float) getX(), (float) (getY() + cursorY));

		// Draw the cursor
		int cursorWidth = getWidth();
		int cursorHeight = cursorWidth / 4;
		graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				CURSOR,
				0, -2,
				cursorWidth, cursorHeight
		);

		matrices.popMatrix();
	}

	@Override
	public void onClick(MouseButtonEvent click, boolean doubleClick) {
		long window = MINECRAFT.getWindow().handle();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
		moveCursor(click.y());
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
		moveCursor(click.y());
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
		long window = MINECRAFT.getWindow().handle();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		isDraggingCursor = false;
	}

	private void moveCursor(double mouseY) {
		cursorY = Math.clamp(mouseY, getY(), getBottom()) - getY();
		updateHue(cursorY);

		colorPicker.onUpdateColor(ColorPicker.ColorPickerElement.HUE);
	}

	private void updateHue(double cursorY) {
		selectedHue = (float) ((cursorY) * 360.0f / getHeight());
	}

	public void updateHue(int color) {
		float[] hsb = Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, null);
		selectedHue = hsb[0] * 360.0f;
		this.cursorY = (hsb[0] * height);
	}

	public boolean isDraggingCursor() {
		return isDraggingCursor;
	}

	public float getSelectedHue() {
		return selectedHue;
	}

	@Override
	protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
	}
}
