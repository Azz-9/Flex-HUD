package me.Azz_9.better_hud.screens.widgets.colorSelector;

import me.Azz_9.better_hud.screens.widgets.buttons.ColorButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class GradientWidget extends ClickableWidget {
	private float selectedHue;
	private int selectedColor;
	private double cursorX;
	private double cursorY;
	private final ColorButtonWidget COLOR_BUTTON_WIDGET;
	private ColorEntryWidget colorEntryWidget;

	public GradientWidget(int width, int height, ColorButtonWidget colorButtonWidget) {
		super(0, 0, width, height, Text.translatable("better_hud.gradient_widget"));
		this.COLOR_BUTTON_WIDGET = colorButtonWidget;
		this.selectedColor = colorButtonWidget.getColor();

		setCursorPositionToSelectedColor();
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		renderGradient(context);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(cursorX, cursorY, 0);

		// Draw the cursor
		context.drawTexture(RenderLayer::getGuiTexturedOverlay, Identifier.of(MOD_ID, "widgets/color_selector/gradient_cursor.png"),
				-3, -3, 0, 0, 6, 6, 6, 6);

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
		moveCursor(mouseX, mouseY);
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		moveCursor(mouseX, mouseY);
	}

	private void moveCursor(double mouseX, double mouseY) {
		cursorX = Math.clamp(mouseX, getX(), getRight());
		cursorY = Math.clamp(mouseY, getY(), getBottom());
		updateColor(cursorX, cursorY);
	}

	private void updateColor(double cursorX, double cursorY) {
		float saturation = (float) (cursorX - getX()) / getWidth();
		float brightness = 1.0f - (float) (cursorY - getY()) / getHeight();

		selectedColor = Color.HSBtoRGB(selectedHue / 360.0f, saturation, brightness) & 0x00ffffff;

		COLOR_BUTTON_WIDGET.setColor(selectedColor);
		colorEntryWidget.setText("#" + Integer.toHexString(selectedColor));
	}

	public void setCursorPositionToSelectedColor() {
		float[] hsbValues = new float[3];
		Color.RGBtoHSB((selectedColor >> 16) & 0xFF, (selectedColor >> 8) & 0xFF, selectedColor & 0xFF, hsbValues);
		float saturation = hsbValues[1];
		float brightness = hsbValues[2];

		cursorX = getX() + saturation * getWidth();
		cursorY = getY() + (1.0f - brightness) * getHeight();
	}

	public void setColor(int color) {
		selectedColor = color;
		setCursorPositionToSelectedColor();
		COLOR_BUTTON_WIDGET.setColor(selectedColor);
	}

	public void setHue(float hue) {
		selectedHue = hue;
		updateColor(cursorX, cursorY);
	}

	public void setColorEntryWidget(ColorEntryWidget colorEntryWidget) {
		this.colorEntryWidget = colorEntryWidget;
	}

	public ColorButtonWidget getCOLOR_BUTTON_WIDGET() {
		return COLOR_BUTTON_WIDGET;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {

	}
}
