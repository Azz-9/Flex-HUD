package me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor;

import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorSelector;
import me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.CrosshairButtonWidget;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.actions.UndoManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class CrosshairEditor implements Element, Drawable, Widget {
	private final Pixel[][] pixels;
	private final CrosshairButtonWidget<?> crosshairButtonWidget;
	private boolean isOpened;

	private int width;
	private int height;
	private int x;
	private int y;

	// color button
	private final TextWidget colorText;
	private final ColorButton colorButton;
	private ColorSelector colorSelector;
	private boolean isDraggingCursor = false;

	// clear button
	private ButtonWidget clearButton;

	// presets list
	private final TextWidget presetText;
	private CrosshairPresetsList crosshairPresetsList;

	private boolean clicked = false;
	private int[][] onClickTexture;

	UndoManager undoManager = new UndoManager();

	public CrosshairEditor(CrosshairButtonWidget<?> crosshairButtonWidget) {
		this.crosshairButtonWidget = crosshairButtonWidget;
		int textureSize = crosshairButtonWidget.getData().length;
		this.pixels = new Pixel[textureSize][textureSize];
		int pixelSize = (MinecraftClient.getInstance().getWindow().getScaledHeight() - 100) / textureSize;

		int padding = 4;
		this.width = textureSize * pixelSize + padding * 2;
		this.height = textureSize * pixelSize + padding * 2;

		this.x = (MinecraftClient.getInstance().getWindow().getScaledWidth() - width) / 2;
		this.y = (MinecraftClient.getInstance().getWindow().getScaledHeight() - height) / 2;

		// pixels
		for (int pixelY = 0; pixelY < textureSize; pixelY++) {
			for (int pixelX = 0; pixelX < textureSize; pixelX++) {
				pixels[pixelY][pixelX] = new Pixel(x + padding + pixelSize * pixelX, y + padding + pixelSize * pixelY, pixelSize, pixelSize, crosshairButtonWidget.getData()[pixelY][pixelX], pixelX, pixelY, pixelX == Math.floor(textureSize / 2.0) || pixelY == Math.floor(textureSize / 2.0), this);
			}
		}

		int asideX = this.getX() + padding * 2 + textureSize * pixelSize;
		int asideWidth = 60;

		// color button
		int colorButtonBlockMargin = 20;
		int colorButtonSize = 20;

		colorText = new TextWidget(Text.translatable("flex_hud.crosshair_editor.color"), MinecraftClient.getInstance().textRenderer);
		colorText.setPosition(asideX, y + colorButtonBlockMargin + (colorButtonSize - MinecraftClient.getInstance().textRenderer.fontHeight) / 2);

		this.colorButton = new ColorButton(
				colorText.getRight() + 2, y + colorButtonBlockMargin,
				colorButtonSize, colorButtonSize,
				() -> colorSelector.setFocused(!colorSelector.isFocused())
		);
		this.colorSelector = new ColorSelector(this.colorButton);
		this.colorSelector.setPosition(colorButton.getX(), colorButton.getBottom());
		this.colorSelector.setFocused(false);

		this.width += asideWidth + padding;

		// clear button
		clearButton = ButtonWidget.builder(Text.translatable("flex_hud.crosshair_editor.clear"), (btn) -> this.clearTexture())
				.position(asideX, colorButton.getBottom() + colorButtonBlockMargin)
				.size(asideWidth, 20)
				.build();

		// presets list
		int listY = colorSelector.getBottom() + 10;
		int listHeight = Math.max(16, this.getBottom() - listY - padding);
		crosshairPresetsList = new CrosshairPresetsList(
				asideWidth - 6, listHeight,
				this.getBottom() - padding - listHeight, asideX, this
		);
		this.presetText = new TextWidget(Text.translatable("flex_hud.crosshair_editor.presets"), MinecraftClient.getInstance().textRenderer);
		this.presetText.setPosition(crosshairPresetsList.getX(), crosshairPresetsList.getY() - MinecraftClient.getInstance().textRenderer.fontHeight - 2);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		context.fill(getX(), getY(), getRight(), getBottom(), 0xff4a4a4a);

		for (int y = 0; y < pixels.length; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				pixels[y][x].render(context, mouseX, mouseY, deltaTicks);
			}
		}

		colorText.render(context, mouseX, mouseY, deltaTicks);
		colorButton.render(context, mouseX, mouseY, deltaTicks);

		clearButton.render(context, mouseX, mouseY, deltaTicks);
		
		presetText.render(context, mouseX, mouseY, deltaTicks);
		crosshairPresetsList.render(context, mouseX, mouseY, deltaTicks);

		if (colorSelector.isFocused()) {
			colorSelector.render(context, mouseX, mouseY, deltaTicks);
		}
	}

	public void onTextureChange(int x, int y) {
		crosshairButtonWidget.onReceivePixel(x, y, pixels[y][x].getColor());
	}

	public int[][] getTexture() {
		int[][] texture = new int[this.pixels.length][this.pixels[0].length];
		for (int y = 0; y < texture.length; y++) {
			texture[y] = crosshairButtonWidget.getData()[y].clone();
		}
		return texture;
	}

	public void setTexture(int[][] texture) {
		crosshairButtonWidget.setCrosshairTexture(texture);
		this.updateTexture(texture);
	}

	public void clearTexture() {
		int[][] texture = new int[this.pixels.length][this.pixels[0].length];
		int[][] oldTexture = getTexture();
		undoManager.addAction(new TextureAction(this, oldTexture, texture));

		crosshairButtonWidget.setCrosshairTexture(texture);
		this.updateTexture(texture);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (colorSelector.isFocused() && colorSelector.mouseClicked(mouseX, mouseY, button)) {
			isDraggingCursor = true;
			return true;
		} else if (this.isMouseOver(mouseX, mouseY)) {
			if (colorButton.mouseClicked(mouseX, mouseY, button) ||
					clearButton.mouseClicked(mouseX, mouseY, button) ||
					crosshairPresetsList.mouseClicked(mouseX, mouseY, button)
			) {
				return true;
			}

			for (int y = 0; y < pixels.length; y++) {
				for (int x = 0; x < pixels[y].length; x++) {
					if (pixels[y][x].isMouseOver(mouseX, mouseY) && (button == 1 || button == 0)) {
						int[][] texture = new int[crosshairButtonWidget.getData().length][crosshairButtonWidget.getData()[0].length];
						for (int i = 0; i < crosshairButtonWidget.getData().length; i++) {
							texture[i] = crosshairButtonWidget.getData()[i].clone();
						}
						onClickTexture = texture;
						pixels[y][x].mouseClicked(mouseX, mouseY, button);
						clicked = true;
						return true;
					}
				}
			}
			colorSelector.setFocused(false);

			return true;
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
			return false;
		} else if (colorSelector.isFocused() && colorSelector.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			return true;
		} else return crosshairPresetsList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if ((this.isMouseOver(mouseX, mouseY) || clicked) && !isDraggingCursor) {
			if (clicked) {
				clicked = false;

				undoManager.addAction(new TextureAction(this, onClickTexture, this.crosshairButtonWidget.getData()));
			}
			return true;
		} else if (colorSelector.isFocused() && colorSelector.mouseReleased(mouseX, mouseY, button)) {
			isDraggingCursor = false;
			return true;
		} else return crosshairPresetsList.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		return crosshairPresetsList.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 87 && modifiers == GLFW.GLFW_MOD_CONTROL) { // CTRL + Z
			undoManager.undo();
			return true;
		} else if ((keyCode == GLFW.GLFW_KEY_Y && modifiers == GLFW.GLFW_MOD_CONTROL) ||
				(keyCode == 87 && modifiers == (GLFW.GLFW_MOD_CONTROL + GLFW.GLFW_MOD_SHIFT))) { // CTRL + Y or CTRL + SHIFT + Z
			undoManager.redo();
			return true;
		}

		return Element.super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void updateTexture(int[][] texture) {
		for (int y = 0; y < texture.length; y++) {
			for (int x = 0; x < texture[y].length; x++) {
				this.pixels[y][x].setColor(texture[y][x]);
			}
		}
	}

	public int getColor() {
		return colorButton.getColor();
	}

	public Pixel[][] getPixels() {
		return pixels;
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
		consumer.accept(colorButton);
		consumer.accept(crosshairPresetsList);
	}
}
