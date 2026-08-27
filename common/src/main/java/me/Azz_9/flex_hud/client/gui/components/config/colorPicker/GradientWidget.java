package me.Azz_9.flex_hud.client.gui.components.config.colorPicker;

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
import org.lwjgl.sdl.SDLMouse;

import java.awt.*;

import me.Azz_9.flex_hud.client.gui.Cursors;

public class GradientWidget extends AbstractWidget.WithInactiveMessage {

	private static final Identifier CURSOR = Identifier.fromNamespaceAndPath(MOD_ID, "widget/color_picker/gradient_cursor");

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
	protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.isActive() && this.isHovered()) {
			graphics.requestCursor(Cursors.CROSSHAIR);
		}

		Matrix3x2fStack matrices = graphics.pose();
		graphics.guiRenderState.addGuiElement(
				new ColorPickerGradientRenderState(
						matrices,
						getX(), getY(),
						getRight(), getBottom(),
						selectedHue / 360.0f,
						graphics.scissorStack.peek()
				)
		);

		matrices.pushMatrix();
		matrices.translate((float) (cursorX + getX()), (float) (cursorY + getY()));

		// Draw the cursor
		graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED,
				CURSOR,
				-CURSOR_SIZE / 2, -CURSOR_SIZE / 2,
				CURSOR_SIZE, CURSOR_SIZE
		);

		matrices.popMatrix();
	}

	@Override
	public void onClick(MouseButtonEvent click, boolean bl) {
		SDLMouse.SDL_HideCursor();
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
		SDLMouse.SDL_ShowCursor();
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
	protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
	}
}
