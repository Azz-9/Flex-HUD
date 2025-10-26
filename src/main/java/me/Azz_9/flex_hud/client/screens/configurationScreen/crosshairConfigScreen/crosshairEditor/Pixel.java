package me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

public class Pixel extends ClickableWidget {
	private CrosshairEditor crosshairEditor;
	private int color;
	private int pixelX, pixelY;
	private boolean isCenter;

	public Pixel(int x, int y, int width, int height, int color, int pixelX, int pixelY, boolean isCenter, CrosshairEditor crosshairEditor) {
		super(x, y, width, height, Text.empty());
		this.crosshairEditor = crosshairEditor;
		this.color = color;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.isCenter = isCenter;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (color >> 24 == 0) {
			context.fill(RenderLayer.getGuiOverlay(), getX() + getWidth() / 4, getY() + getHeight() / 4, getRight() - getWidth() / 4, getBottom() - getHeight() / 4, (isCenter ? 0xff424242 : 0xff3a3a3a));
		} else {
			context.fill(RenderLayer.getGuiOverlay(), getX(), getY(), getRight(), getBottom(), color);
		}


		int color;
		if (this.isHovered()) {
			color = 0xffdf1515;
		} else {
			color = 0xffdfdfdf;
		}

		context.fill(RenderLayer.getGuiOverlay(), getX(), getY(), getX() + getWidth(), getY() + 1, color);
		context.fill(RenderLayer.getGuiOverlay(), getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight(), color);
		context.fill(RenderLayer.getGuiOverlay(), getX(), getY() + 1, getX() + 1, getY() + getHeight() - 1, color);
		context.fill(RenderLayer.getGuiOverlay(), getX() + getWidth() - 1, getY() + 1, getX() + getWidth(), getY() + getHeight() - 1, color);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isMouseOver(mouseX, mouseY)) {
			if (button == 0) {
				if (color != crosshairEditor.getColor()) {
					color = crosshairEditor.getColor();
					crosshairEditor.onTextureChange(pixelX, pixelY);
				}
			} else if (button == 1) {
				if (color != 0x00000000) {
					color = 0x00000000;
					crosshairEditor.onTextureChange(pixelX, pixelY);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return mouseClicked(mouseX, mouseY, button);
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}
}
