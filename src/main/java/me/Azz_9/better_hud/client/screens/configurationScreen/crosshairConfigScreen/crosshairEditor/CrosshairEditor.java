package me.Azz_9.better_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor;

import me.Azz_9.better_hud.client.screens.configurationScreen.crosshairConfigScreen.CrosshairButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

public class CrosshairEditor implements Element, Drawable, Widget {
	private final Pixel[][] pixels;
	private CrosshairButtonWidget<?> crosshairButtonWidget;
	private boolean isOpened;

	private int pixelSize;
	private int padding = 4;

	private int width;
	private int height;
	private int x;
	private int y;

	private boolean clicked = false;

	public CrosshairEditor(CrosshairButtonWidget<?> crosshairButtonWidget) {
		this.crosshairButtonWidget = crosshairButtonWidget;
		int textureSize = crosshairButtonWidget.getData().length;
		this.pixels = new Pixel[textureSize][textureSize];
		this.pixelSize = (MinecraftClient.getInstance().getWindow().getScaledHeight() - 100) / textureSize;

		this.width = textureSize * pixelSize + padding * 2;
		this.height = textureSize * pixelSize + padding * 2;

		this.x = (MinecraftClient.getInstance().getWindow().getScaledWidth() - width) / 2;
		this.y = (MinecraftClient.getInstance().getWindow().getScaledHeight() - height) / 2;

		for (int pixelY = 0; pixelY < textureSize; pixelY++) {
			for (int pixelX = 0; pixelX < textureSize; pixelX++) {
				pixels[pixelY][pixelX] = new Pixel(x + padding + pixelSize * pixelX, y + padding + pixelSize * pixelY, pixelSize, pixelSize, crosshairButtonWidget.getData()[pixelY][pixelX], pixelX, pixelY, pixelX == Math.floor(textureSize / 2.0) || pixelY == Math.floor(textureSize / 2.0), this);
			}
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		context.fill(getX(), getY(), getRight(), getBottom(), 0xff4a4a4a);

		for (int y = 0; y < pixels.length; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				pixels[y][x].render(context, mouseX, mouseY, deltaTicks);
			}
		}
	}

	public void onTextureChange(int x, int y) {
		crosshairButtonWidget.onReceivePixel(x, y, pixels[y][x].getColor());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.isMouseOver(mouseX, mouseY)) {
			clicked = true;
			for (int y = 0; y < pixels.length; y++) {
				for (int x = 0; x < pixels[y].length; x++) {
					if (pixels[y][x].mouseClicked(mouseX, mouseY, button)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isMouseOver(mouseX, mouseY) && clicked) {
			for (int y = 0; y < pixels.length; y++) {
				for (int x = 0; x < pixels[y].length; x++) {
					if (pixels[y][x].mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.isMouseOver(mouseX, mouseY) || clicked) {
			clicked = false;
			return true;
		}
		return false;
	}

	public void updateTexture(int[][] pixels) {
		for (int y = 0; y < pixels.length; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				this.pixels[y][x].setColor(pixels[y][x]);
			}
		}
	}

	@Override
	public void setFocused(boolean isOpened) {
		this.isOpened = isOpened;
	}

	@Override
	public boolean isFocused() {
		return isOpened;
	}

	@Override
	public ScreenRect getNavigationFocus() {
		return Element.super.getNavigationFocus();
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public int getRight() {
		return x + width;
	}

	public int getBottom() {
		return y + height;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= getX() && mouseX <= getRight() && mouseY >= getY() && mouseY <= getBottom();
	}

	@Override
	public void forEachChild(Consumer<ClickableWidget> consumer) {
		for (int y = 0; y < pixels.length; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				consumer.accept(pixels[y][x]);
			}
		}
	}
}
