package me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor;

import me.Azz_9.flex_hud.client.utils.Cursors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class Pixel extends AbstractWidget.WithInactiveMessage {
	private CrosshairEditor crosshairEditor;
	private int color;
	private int pixelX, pixelY;
	private boolean isCenter;

	public Pixel(int x, int y, int width, int height, int color, int pixelX, int pixelY, boolean isCenter, CrosshairEditor crosshairEditor) {
		super(x, y, width, height, Component.empty());
		this.crosshairEditor = crosshairEditor;
		this.color = color;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.isCenter = isCenter;
	}

	@Override
	protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.isHovered()) {
			graphics.requestCursor(Cursors.POINTING_HAND);
		}

		if (color >> 24 == 0) {
			graphics.fill(getX() + getWidth() / 4, getY() + getHeight() / 4, getRight() - getWidth() / 4, getBottom() - getHeight() / 4, (isCenter ? 0xff424242 : 0xff3a3a3a));
		} else {
			graphics.fill(getX(), getY(), getRight(), getBottom(), color);
		}


		if (this.isHovered()) {
			graphics.renderOutline(getX(), getY(), getWidth(), getHeight(), 0xffdf1515);
		} else {
			graphics.renderOutline(getX(), getY(), getWidth(), getHeight(), 0xffdfdfdf);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		if (isMouseOver(click.x(), click.y())) {
			if (click.button() == 0) {
				if (color != crosshairEditor.getColor()) {
					color = crosshairEditor.getColor();
					crosshairEditor.onTextureChange(pixelX, pixelY);
				}
			} else if (click.button() == 1) {
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
	public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
		return mouseClicked(click, false);
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	@Override
	protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
	}
}
