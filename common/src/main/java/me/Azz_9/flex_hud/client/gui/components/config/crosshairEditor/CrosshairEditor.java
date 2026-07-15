package me.Azz_9.flex_hud.client.gui.components.config.crosshairEditor;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.function.Consumer;

import me.Azz_9.flex_hud.client.gui.Cursors;
import me.Azz_9.flex_hud.client.gui.components.HelpWidget;
import me.Azz_9.flex_hud.client.gui.components.config.buttons.CrosshairButtonWidget;
import me.Azz_9.flex_hud.client.gui.components.config.colorSelector.ColorSelector;
import me.Azz_9.flex_hud.client.gui.undoManager.TextureAction;
import me.Azz_9.flex_hud.client.gui.undoManager.UndoManager;

public class CrosshairEditor extends AbstractWidget {
	private static final int PADDING = 4;

	// help widget
	private static final int HELP_WIDGET_PADDING = 4;
	private static final int HELP_WIDGET_SIZE = 20;
	private HelpWidget helpWidget;

	private final Pixel[][] pixels;
	private final CrosshairButtonWidget<?> crosshairButtonWidget;
	private boolean isOpened;

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
		super(0, 0, 0, 0, Component.empty());
		this.crosshairButtonWidget = crosshairButtonWidget;

		helpWidget = new HelpWidget(HELP_WIDGET_PADDING, this.height - HELP_WIDGET_PADDING - HELP_WIDGET_SIZE, HELP_WIDGET_SIZE, HELP_WIDGET_SIZE, new Component[]{
				Component.translatable("flex_hud.crosshair_editor.help_widget.line1"),
				Component.translatable("flex_hud.crosshair_editor.help_widget.line2"),
				Component.translatable("flex_hud.crosshair_editor.help_widget.line3"),
				Component.translatable("flex_hud.crosshair_editor.help_widget.line4"),
		});

		int textureSize = crosshairButtonWidget.getData().length;
		this.pixels = new Pixel[textureSize][textureSize];
		int pixelSize = (MINECRAFT.getWindow().getGuiScaledHeight() - 100) / textureSize;

		setWidth(textureSize * pixelSize + PADDING * 2);
		setHeight(textureSize * pixelSize + PADDING * 2);

		setX((MINECRAFT.getWindow().getGuiScaledWidth() - width) / 2);
		setY((MINECRAFT.getWindow().getGuiScaledHeight() - height) / 2);

		// pixels
		for (int pixelY = 0; pixelY < textureSize; pixelY++) {
			for (int pixelX = 0; pixelX < textureSize; pixelX++) {
				pixels[pixelY][pixelX] = new Pixel(getX() + PADDING + pixelSize * pixelX, getY() + PADDING + pixelSize * pixelY, pixelSize, pixelSize, crosshairButtonWidget.getData()[pixelY][pixelX], pixelX, pixelY, pixelX == Math.floor(textureSize / 2.0) || pixelY == Math.floor(textureSize / 2.0), this);
			}
		}

		int asideX = this.getX() + PADDING * 2 + textureSize * pixelSize;
		int asideWidth = 60;

		// color button
		int colorButtonBlockMargin = 20;
		int colorButtonSize = 20;

		colorText = new StringWidget(Component.translatable("flex_hud.crosshair_editor.color"), MINECRAFT.font);
		colorText.setPosition(asideX, getY() + colorButtonBlockMargin + (colorButtonSize - MINECRAFT.font.lineHeight) / 2);

		this.colorButton = new ColorButton(
				colorText.getRight() + 2, getY() + colorButtonBlockMargin,
				colorButtonSize, colorButtonSize,
				() -> colorSelector.setFocused(!colorSelector.isFocused())
		);
		this.colorSelector = new ColorSelector(this.colorButton);
		this.colorSelector.setPosition(colorButton.getX(), colorButton.getBottom());
		this.colorSelector.setFocused(false);

		this.width += asideWidth + PADDING;

		// clear button
		clearButton = Button.builder(Component.translatable("flex_hud.crosshair_editor.clear"), (btn) -> this.clearTexture())
				.pos(asideX, colorButton.getBottom() + colorButtonBlockMargin)
				.size(asideWidth, 20)
				.build();

		// presets list
		int listY = colorSelector.getBottom() + 10;
		int listHeight = Math.max(16, this.getBottom() - listY - PADDING);
		crosshairPresetsList = new CrosshairPresetsList(
				asideWidth - 6, listHeight,
				this.getBottom() - PADDING - listHeight, asideX, this
		);
		this.presetText = new StringWidget(Component.translatable("flex_hud.crosshair_editor.presets"), MINECRAFT.font);
		this.presetText.setPosition(crosshairPresetsList.getX(), crosshairPresetsList.getY() - MINECRAFT.font.lineHeight - 2);
	}

	@Override
	protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		helpWidget.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		if (this.isMouseOver(mouseX, mouseY)) {
			graphics.requestCursor(Cursors.DEFAULT);
		}

		graphics.fill(getX(), getY(), getRight(), getBottom(), 0xff4a4a4a);

		for (int y = 0; y < pixels.length; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				pixels[y][x].extractRenderState(graphics, mouseX, mouseY, deltaTicks);
			}
		}

		colorText.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		colorButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		clearButton.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		presetText.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		crosshairPresetsList.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		if (colorSelector.isFocused()) {
			colorSelector.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
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
		helpWidget.handleOutsideClick(click, doubled);
		if (colorSelector.isFocused() && colorSelector.mouseClicked(click, doubled)) {
			isDraggingCursor = true;
			return true;

		} else if (this.isMouseOver(click.x(), click.y())) {
			if (helpWidget.mouseClicked(click, doubled)) {
				return true;
			}
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
	public boolean isMouseOver(double mouseX, double mouseY) {
		return super.isMouseOver(mouseX, mouseY) || helpWidget.isMouseOver(mouseX, mouseY);
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
	public boolean keyPressed(@NotNull KeyEvent input) {
		if (undoManager.handleKeyPressed(input)) {
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
	protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
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
