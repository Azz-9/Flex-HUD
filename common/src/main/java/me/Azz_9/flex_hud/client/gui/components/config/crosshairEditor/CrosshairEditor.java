package me.Azz_9.flex_hud.client.gui.components.config.crosshairEditor;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;

import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.components.HelpWidget;
import me.Azz_9.flex_hud.client.gui.components.config.Popup;
import me.Azz_9.flex_hud.client.gui.components.config.buttons.CrosshairButtonWidget;
import me.Azz_9.flex_hud.client.gui.components.config.colorSelector.ColorSelector;
import me.Azz_9.flex_hud.client.gui.screens.AbstractPopupScreen;
import me.Azz_9.flex_hud.client.gui.undoManager.TextureAction;
import me.Azz_9.flex_hud.client.gui.undoManager.UndoManager;

public class CrosshairEditor extends AbstractWidget implements Popup {
	private static final int PADDING = 4;
	private static final int DONE_BUTTON_WIDTH = 200;
	private static final int DONE_BUTTON_HEIGHT = 20;
	private static final int DONE_BUTTON_MARGIN = 10;
	private static final int ASIDE_WIDTH = 60;

	// help widget
	private static final int HELP_WIDGET_PADDING = 4;
	private static final int HELP_WIDGET_SIZE = 20;
	private final HelpWidget helpWidget;

	private final Pixel[][] pixels;
	private final CrosshairButtonWidget<?> crosshairButtonWidget;
	private boolean isOpened;

	// color button
	private static final int COLOR_BUTTON_SIZE = 20;
	private final StringWidget colorText;
	private final ColorButton colorButton;
	private ColorSelector colorSelector;
	private boolean isDraggingCursor = false;

	// clear button
	private static final int CLEAR_BUTTON_HEIGHT = 20;
	private final Button clearButton;

	// presets list
	private static final int PRESETS_LIST_MIN_HEIGHT = 32;
	private final CrosshairPresetsList crosshairPresetsList;

	private final Button done;

	private boolean clicked = false;
	private int[][] onClickTexture;

	UndoManager undoManager = new UndoManager();

	public CrosshairEditor(CrosshairButtonWidget<?> crosshairButtonWidget) {
		super(0, 0, 0, 0, Component.empty());
		this.crosshairButtonWidget = crosshairButtonWidget;

		helpWidget = new HelpWidget(
				HELP_WIDGET_PADDING,
				MINECRAFT.getWindow().getGuiScaledHeight() - HELP_WIDGET_PADDING - HELP_WIDGET_SIZE,
				HELP_WIDGET_SIZE,
				HELP_WIDGET_SIZE,
				new Component[]{
						Component.translatable("flex_hud.crosshair_editor.help_widget.line1"),
						Component.translatable("flex_hud.crosshair_editor.help_widget.line2"),
						Component.translatable("flex_hud.crosshair_editor.help_widget.line3"),
						Component.translatable("flex_hud.crosshair_editor.help_widget.line4"),
				}
		);

		int textureSize = crosshairButtonWidget.getData().length;
		this.pixels = new Pixel[textureSize][textureSize];
		int pixelSize = (MINECRAFT.getWindow().getGuiScaledHeight() - 100) / textureSize;

		setWidth(textureSize * pixelSize + PADDING * 2 + ASIDE_WIDTH + PADDING);
		setHeight(textureSize * pixelSize + PADDING * 2 + DONE_BUTTON_HEIGHT + DONE_BUTTON_MARGIN * 2);

		setX((MINECRAFT.getWindow().getGuiScaledWidth() - width) / 2);
		setY((MINECRAFT.getWindow().getGuiScaledHeight() - height) / 2);

		// pixels
		for (int pixelY = 0; pixelY < textureSize; pixelY++) {
			for (int pixelX = 0; pixelX < textureSize; pixelX++) {
				pixels[pixelY][pixelX] = new Pixel(getX() + PADDING + pixelSize * pixelX, getY() + PADDING + pixelSize * pixelY, pixelSize, pixelSize, crosshairButtonWidget.getData()[pixelY][pixelX], pixelX, pixelY, pixelX == Math.floor(textureSize / 2.0) || pixelY == Math.floor(textureSize / 2.0), this);
			}
		}

		int asideX = this.getX() + PADDING * 2 + textureSize * pixelSize;

		// color button
		int colorButtonBlockMargin = 20;

		colorText = new StringWidget(Component.translatable("flex_hud.crosshair_editor.color"), MINECRAFT.font);
		colorText.setPosition(asideX, getY() + colorButtonBlockMargin + (COLOR_BUTTON_SIZE - MINECRAFT.font.lineHeight) / 2);

		this.colorButton = new ColorButton(
				colorText.getRight() + 2, getY() + colorButtonBlockMargin,
				COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE,
				() -> colorSelector.setFocused(!colorSelector.isFocused())
		);
		this.colorSelector = new ColorSelector(this.colorButton);
		this.colorSelector.setPosition(colorButton.getX(), colorButton.getBottom());
		this.colorSelector.setFocused(false);

		// clear button
		clearButton = Button.builder(Component.translatable("flex_hud.crosshair_editor.clear"), ignored -> this.clearTexture())
				.pos(asideX, colorButton.getBottom() + colorButtonBlockMargin)
				.size(ASIDE_WIDTH, CLEAR_BUTTON_HEIGHT)
				.build();

		// presets list
		int listY = colorSelector.getBottom() + 10;
		int listHeight = Math.max(PRESETS_LIST_MIN_HEIGHT, this.getBottom() - listY - PADDING);
		crosshairPresetsList = new CrosshairPresetsList(
				ASIDE_WIDTH - 6, listHeight,
				this.getBottom() - PADDING - listHeight, asideX, this
		);

		done = Button.builder(CommonComponents.GUI_DONE, ignored -> {
					if (MINECRAFT.screen instanceof AbstractPopupScreen screen) {
						screen.closePopup();
					}
				})
				.bounds(
						getX() + ((pixelSize * textureSize + PADDING * 2) - DONE_BUTTON_WIDTH) / 2,
						getY() + height - DONE_BUTTON_MARGIN - DONE_BUTTON_HEIGHT,
						DONE_BUTTON_WIDTH, DONE_BUTTON_HEIGHT
				)
				.build();
	}

	@Override
	protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		//overlay
		graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), Colors.BLACK_TRANSPARENT);

		helpWidget.render(graphics, mouseX, mouseY, deltaTicks);

		graphics.fill(getX(), getY(), getRight(), getBottom(), 0xff4a4a4a);

		for (int y = 0; y < pixels.length; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				pixels[y][x].render(graphics, mouseX, mouseY, deltaTicks);
			}
		}

		colorText.render(graphics, mouseX, mouseY, deltaTicks);
		colorButton.render(graphics, mouseX, mouseY, deltaTicks);

		clearButton.render(graphics, mouseX, mouseY, deltaTicks);

		graphics.drawString(MINECRAFT.font, Component.translatable("flex_hud.crosshair_editor.presets"), crosshairPresetsList.getX(), crosshairPresetsList.getY() - MINECRAFT.font.lineHeight - 2, Colors.WHITE);
		crosshairPresetsList.render(graphics, mouseX, mouseY, deltaTicks);

		if (colorSelector.isFocused()) {
			colorSelector.render(graphics, mouseX, mouseY, deltaTicks);
		}

		done.render(graphics, mouseX, mouseY, deltaTicks);
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		helpWidget.handleOutsideClick(mouseX, mouseY);
		if (colorSelector.isFocused() && colorSelector.mouseClicked(mouseX, mouseY, button)) {
			isDraggingCursor = true;
			return true;

		} else if (this.isMouseOver(mouseX, mouseY)) {
			if (helpWidget.mouseClicked(mouseX, mouseY, button)) {
				return true;
			}
			if (colorButton.mouseClicked(mouseX, mouseY, button)) {
				return true;
			}
			if (clearButton.mouseClicked(mouseX, mouseY, button)
					|| crosshairPresetsList.isMouseOver(mouseX, mouseY) && crosshairPresetsList.mouseClicked(mouseX, mouseY, button)
					|| done.mouseClicked(mouseX, mouseY, button)) {
				colorSelector.setFocused(false);
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
	public boolean isMouseOver(double mouseX, double mouseY) {
		return super.isMouseOver(mouseX, mouseY) || helpWidget.isMouseOver(mouseX, mouseY);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (this.isMouseOver(mouseX, mouseY) && clicked) {
			for (int y = 0; y < pixels.length; y++) {
				for (int x = 0; x < pixels[y].length; x++) {
					if (pixels[y][x].mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
						return true;
					}
				}
			}
			return false;
		} else if (colorSelector.isFocused() && colorSelector.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
			return true;
		} else return crosshairPresetsList.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if ((this.isMouseOver(mouseX, mouseY) || clicked) && !isDraggingCursor) {
			if (clicked) {
				clicked = false;

				if (!Arrays.deepEquals(onClickTexture, getTexture())) {
					undoManager.addAction(new TextureAction(this, onClickTexture, getTexture()));
				}
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
		if (undoManager.handleKeyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}

		return colorSelector.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		return colorSelector.charTyped(codePoint, modifiers);
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
	public void onClose() {
		colorSelector.setFocused(false);
		setFocused(false);
	}

	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
	}

	@Override
	public void visitWidgets(@NotNull Consumer<AbstractWidget> widgetVisitor) {
		for (int y = 0; y < pixels.length; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				widgetVisitor.accept(pixels[y][x]);
			}
		}
		widgetVisitor.accept(colorButton);
		widgetVisitor.accept(crosshairPresetsList);
	}
}
