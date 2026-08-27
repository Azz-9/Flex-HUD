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

public class HueWidget extends AbstractWidget {

	private static final ResourceLocation CURSOR = ResourceLocation.fromNamespaceAndPath(MOD_ID, "widget/color_picker/hue_cursor");

	private float selectedHue;
	private double cursorY;

	private boolean isDraggingCursor = false;

	private final ColorUpdatable colorPicker;

	HueWidget(int width, int height, ColorUpdatable colorPicker) {
		super(0, 0, width, height, Component.translatable("flex_hud.hue_bar"));
		this.colorPicker = colorPicker;
	}

	@Override
	protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		DrawingUtils.drawColorPickerHueBar(graphics, getX(), getY(), getWidth(), getHeight());

		PoseStack matrices = graphics.pose();
		matrices.pushPose();
		matrices.translate((float) getX(), (float) (getY() + cursorY), 0);

		// Draw the cursor
		int cursorWidth = getWidth();
		int cursorHeight = cursorWidth / 4;
		graphics.blitSprite(
				RenderType::guiTextured,
				CURSOR,
				0, -2,
				cursorWidth, cursorHeight
		);

		matrices.popPose();
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		long window = MINECRAFT.getWindow().getWindow();
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
		moveCursor(mouseY);
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
		moveCursor(mouseY);
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
	protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
	}
}
