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

public class HueBarWidget extends ClickableWidget {
	private float selectedHue;
	private double cursorY = 0;
	private final ColorButtonWidget colorButtonWidget;
	private final GradientWidget gradient;

	public HueBarWidget(int x, int y, int width, int height, ColorButtonWidget colorButtonWidget, GradientWidget gradient) {
		super(x, y, width, height, Text.literal("Hue Bar"));
		this.colorButtonWidget = colorButtonWidget;
		this.gradient = gradient;
		this.selectedHue = getHue(colorButtonWidget.getColor());

		this.cursorY = getY() + (selectedHue / 360.0) * height;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		drawHueBar(context);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getX(), cursorY, 0);

		// Draw the cursor
		context.drawTexture(RenderLayer::getGuiTexturedOverlay, Identifier.of(MOD_ID, "widgets/color_selector/hue_cursor.png"),
				0, -2, 0, 0, 16, 4, 16, 4);

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
		moveCursor(mouseY);
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		moveCursor(mouseY);
	}

	private void moveCursor(double mouseY) {
		cursorY = Math.clamp(mouseY, getY(), getBottom());
		updateHue(cursorY);
	}

	private void updateHue(double cursorY) {
		selectedHue = (float) ((cursorY - getY()) * 360.0f / getHeight());
		gradient.setHue(selectedHue);
	}

	private float getHue(int color) {
		float [] hsb = Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, null);
        return hsb[0] * 360.0f;
	}

	public void setHue(int color) {
		this.selectedHue = getHue(color);
        this.cursorY = (getY() + (selectedHue / 360.0) * height);
		gradient.setHue(selectedHue);
	}

	public ColorButtonWidget getColorButtonWidget() {
		return colorButtonWidget;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {

	}
}
