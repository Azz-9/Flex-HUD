package me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor;

import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorSelector;
import me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.CrosshairButtonWidget;
import me.Azz_9.flex_hud.client.screens.moveModulesScreen.actions.UndoManager;
import me.Azz_9.flex_hud.client.utils.Cursors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.function.Consumer;

public class CrosshairEditor implements GuiEventListener, Renderable, LayoutElement {
	private final Pixel[][] pixels;
	private final CrosshairButtonWidget<?> crosshairButtonWidget;
	private boolean isOpened;

	private int width;
	private int height;
	private int x;
	private int y;

	// color button
	private final StringWidget colorText;
	private final ColorButton colorButton;
	private ColorSelector colorSelector;
	private boolean isDraggingCursor = false;

	// clear button
	private final Button clearButton;

	// presets list
	private final StringWidget presetText;
	private final CrosshairPresetsList crosshairPresetsList;

	private boolean clicked = false;
	private int[][] onClickTexture;

	UndoManager undoManager = new UndoManager();

	public CrosshairEditor(CrosshairButtonWidget<?> crosshairButtonWidget) {
		this.crosshairButtonWidget = crosshairButtonWidget;
		int textureSize = crosshairButtonWidget.getData().length;
		this.pixels = new Pixel[textureSize][textureSize];
		int pixelSize = (Minecraft.getInstance().getWindow().getGuiScaledHeight() - 100) / textureSize;

		int padding = 4;
		this.width = textureSize * pixelSize + padding * 2;
		this.height = textureSize * pixelSize + padding * 2;

		this.x = (Minecraft.getInstance().getWindow().getGuiScaledWidth() - width) / 2;
		this.y = (Minecraft.getInstance().getWindow().getGuiScaledHeight() - height) / 2;

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

		colorText = new StringWidget(Component.translatable("flex_hud.crosshair_editor.color"), Minecraft.getInstance().font);
		colorText.setPosition(asideX, y + colorButtonBlockMargin + (colorButtonSize - Minecraft.getInstance().font.lineHeight) / 2);

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
		clearButton = Button.builder(Component.translatable("flex_hud.crosshair_editor.clear"), (btn) -> this.clearTexture())
				.pos(asideX, colorButton.getBottom() + colorButtonBlockMargin)
				.size(asideWidth, 20)
				.build();

		// presets list
		int listY = colorSelector.getBottom() + 10;
		int listHeight = Math.max(16, this.getBottom() - listY - padding);
		crosshairPresetsList = new CrosshairPresetsList(
				asideWidth - 6, listHeight,
				this.getBottom() - padding - listHeight, asideX, this
		);
		this.presetText = new StringWidget(Component.translatable("flex_hud.crosshair_editor.presets"), Minecraft.getInstance().font);
		this.presetText.setPosition(crosshairPresetsList.getX(), crosshairPresetsList.getY() - Minecraft.getInstance().font.lineHeight - 2);
	}

	@Override
	public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.isMouseOver(mouseX, mouseY)) {
			graphics.requestCursor(Cursors.DEFAULT);
		}

		graphics.fill(getX(), getY(), getRight(), getBottom(), 0xff4a4a4a);

		for (int y = 0; y < pixels.length; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				pixels[y][x].render(graphics, mouseX, mouseY, deltaTicks);
			}
		}

		colorText.render(graphics, mouseX, mouseY, deltaTicks);
		colorButton.render(graphics, mouseX, mouseY, deltaTicks);

		clearButton.render(graphics, mouseX, mouseY, deltaTicks);

		presetText.render(graphics, mouseX, mouseY, deltaTicks);
		crosshairPresetsList.render(graphics, mouseX, mouseY, deltaTicks);

		if (colorSelector.isFocused()) {
			colorSelector.render(graphics, mouseX, mouseY, deltaTicks);
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

		if (!Arrays.deepEquals(oldTexture, texture)) {
			undoManager.addAction(new TextureAction(this, oldTexture, texture));

			crosshairButtonWidget.setCrosshairTexture(texture);
			this.updateTexture(texture);
		}
	}

	public void onPresetUpdate(int[][] newTexture) {

		int[][] oldTexture = getTexture();

		if (!Arrays.deepEquals(oldTexture, newTexture)) {
			setTexture(newTexture);
			undoManager.addAction(new TextureAction(this, oldTexture, newTexture));
		}
	}

	@Override
	public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
		if (colorSelector.isFocused() && colorSelector.mouseClicked(click, doubled)) {
			isDraggingCursor = true;
			return true;

		} else if (this.isMouseOver(click.x(), click.y())) {
			if (colorButton.mouseClicked(click, doubled)) {
				return true;
			}
			if (clearButton.mouseClicked(click, doubled) || crosshairPresetsList.mouseClicked(click, doubled)) {
				colorSelector.setFocused(false);
				return true;
			}

			for (int y = 0; y < pixels.length; y++) {
				for (int x = 0; x < pixels[y].length; x++) {
					if (pixels[y][x].isMouseOver(click.x(), click.y()) && (click.button() == 1 || click.button() == 0)) {
						int[][] texture = new int[crosshairButtonWidget.getData().length][crosshairButtonWidget.getData()[0].length];
						for (int i = 0; i < crosshairButtonWidget.getData().length; i++) {
							texture[i] = crosshairButtonWidget.getData()[i].clone();
						}

						onClickTexture = texture;
						pixels[y][x].mouseClicked(click, doubled);
						clicked = true;

						colorSelector.setFocused(false);
						return true;
					}
				}
			}

			colorSelector.setFocused(false);
			return true;
		}

		if (colorSelector.isFocused()) {
			colorSelector.setFocused(false);
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
		if (this.isMouseOver(click.x(), click.y()) && clicked) {
			for (int y = 0; y < pixels.length; y++) {
				for (int x = 0; x < pixels[y].length; x++) {
					if (pixels[y][x].mouseDragged(click, offsetX, offsetY)) {
						return true;
					}
				}
			}
			return false;
		} else if (colorSelector.isFocused() && colorSelector.mouseDragged(click, offsetX, offsetY)) {
			return true;
		} else return crosshairPresetsList.mouseDragged(click, offsetX, offsetY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent click) {
		if ((this.isMouseOver(click.x(), click.y()) || clicked) && !isDraggingCursor) {
			if (clicked) {
				clicked = false;

				if (!Arrays.deepEquals(onClickTexture, getTexture())) {
					undoManager.addAction(new TextureAction(this, onClickTexture, getTexture()));
				}
			}
			return true;
		} else if (colorSelector.isFocused() && colorSelector.mouseReleased(click)) {
			isDraggingCursor = false;
			return true;
		} else return crosshairPresetsList.mouseReleased(click);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		return crosshairPresetsList.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		if (input.key() == 87 && input.modifiers() == GLFW.GLFW_MOD_CONTROL) { // CTRL + Z
			undoManager.undo();
			return true;
		} else if ((input.key() == GLFW.GLFW_KEY_Y && input.modifiers() == GLFW.GLFW_MOD_CONTROL) ||
				(input.key() == 87 && input.modifiers() == (GLFW.GLFW_MOD_CONTROL + GLFW.GLFW_MOD_SHIFT))) { // CTRL + Y or CTRL + SHIFT + Z
			undoManager.redo();
			return true;
		}

		return colorSelector.keyPressed(input);
	}

	@Override
	public boolean charTyped(@NonNull CharacterEvent input) {
		return colorSelector.charTyped(input);
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
	public @NonNull ScreenRectangle getRectangle() {
		return GuiEventListener.super.getRectangle();
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
	public void visitWidgets(@NonNull Consumer<AbstractWidget> widgetVisitor) {
		for (int y = 0; y < pixels.length; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				widgetVisitor.accept(pixels[y][x]);
			}
		}
		widgetVisitor.accept(colorButton);
		widgetVisitor.accept(crosshairPresetsList);
	}
}
