package me.Azz_9.better_hud.screens.widgets.buttons;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ColorButtonWidget extends ButtonWidget implements TrackableChange {
	private int color;
	private int initialColor;
	public boolean isSelectingColor = false;
	private int colorSelectorX = getX();
	private int colorSelectorY = getY() + getHeight();
	private final int colorSelectorWidth = 120;
	private final int colorSelectorHeight = 124;

	private final Consumer<Integer> consumer;

	public ColorButtonWidget(int x, int y, int width, int height, int currentColor, PressAction onPress, int screenWidth, int screenHeight, Consumer<Integer> consumer) {
		super(x, y, width, height, Text.of(""), onPress, DEFAULT_NARRATION_SUPPLIER);
		this.color = currentColor;
		this.initialColor = currentColor;

		this.consumer = consumer;

		if (colorSelectorX + colorSelectorWidth > screenWidth) {
			colorSelectorX = colorSelectorX - colorSelectorWidth + getWidth();
		}
		if (colorSelectorY + colorSelectorHeight > screenHeight) {
			colorSelectorY = colorSelectorY - getHeight() - colorSelectorHeight;
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
        return colorSelectorWidth;
    }

    public int getColorSelectorHeight() {
        return colorSelectorHeight;
    }

	@Override
	public void onClick(double mouseX, double mouseY) {
		super.onClick(mouseX, mouseY);
		isSelectingColor = !isSelectingColor;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		consumer.accept(color);

		context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color | 0xFF000000);
		if (this.isFocused()) {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xffd0d0d0);
		} else {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xff404040);
		}
	}

	@Override
	public boolean hasChanged() {
		return color != initialColor;
	}

	@Override
	public void cancel() {
		consumer.accept(initialColor);
	}
}