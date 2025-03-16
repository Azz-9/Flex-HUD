package me.Azz_9.better_hud.screens.widgets.buttons;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ColorButtonWidget extends ButtonWidget implements TrackableChange {
	private int color;
	private final int INITIAL_COLOR;
	public boolean isSelectingColor = false;
	private int colorSelectorX = getX();
	private int colorSelectorY = getY() + getHeight();
	private final int COLOR_SELECTOR_WIDTH = 120;
	private final int COLOR_SELECTOR_HEIGHT = 124;

	private final Consumer<Integer> CONSUMER;

	public ColorButtonWidget(int width, int height, int currentColor, PressAction onPress, int screenWidth, int screenHeight, Consumer<Integer> consumer) {
		super(0, 0, width, height, Text.of(""), onPress, DEFAULT_NARRATION_SUPPLIER);
		this.color = currentColor;
		this.INITIAL_COLOR = currentColor;

		this.CONSUMER = consumer;

		if (colorSelectorX + COLOR_SELECTOR_WIDTH > screenWidth) {
			colorSelectorX = colorSelectorX - COLOR_SELECTOR_WIDTH + getWidth();
		}
		if (colorSelectorY + COLOR_SELECTOR_HEIGHT > screenHeight) {
			colorSelectorY = colorSelectorY - getHeight() - COLOR_SELECTOR_HEIGHT;
		}
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public int getColorSelectorX() {
		return colorSelectorX;
	}

	public int getColorSelectorY() {
		return colorSelectorY;
	}

	public int getColorSelectorWidth() {
		return COLOR_SELECTOR_WIDTH;
	}

	public int getColorSelectorHeight() {
		return COLOR_SELECTOR_HEIGHT;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		super.onClick(mouseX, mouseY);
		isSelectingColor = !isSelectingColor;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		CONSUMER.accept(color);

		context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color | 0xFF000000);
		if (this.isFocused()) {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xffd0d0d0);
		} else {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xff404040);
		}
	}

	@Override
	public boolean hasChanged() {
		return color != INITIAL_COLOR;
	}

	@Override
	public void cancel() {
		CONSUMER.accept(INITIAL_COLOR);
	}
}