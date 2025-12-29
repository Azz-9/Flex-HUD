package me.Azz_9.flex_hud.client.screens.widgets.textFieldWidget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class PlaceholderTextFieldWidget extends EditBox {

	private int placeholderColor = 0xffa0a0a0;
	private Component placeholderText;
	private final Font FONT;

	public PlaceholderTextFieldWidget(Font font, int x, int y, int width, int height, Component text) {
		super(font, x, y, width, height, text);
		this.FONT = font;
	}

	public void setPlaceholderColor(int color) {
		this.placeholderColor = color;
	}

	public void setPlaceholder(Component placeholder) {
		this.placeholderText = placeholder;
	}

	@Override
	public void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (this.isVisible()) {
			super.renderWidget(graphics, mouseX, mouseY, delta);

			if (this.placeholderText != null && this.getValue().isEmpty()) {
				int x = this.isBordered() ? this.getX() + 4 : this.getX();
				int y = this.isBordered() ? this.getY() + (this.height - 8) / 2 : this.getY();

				graphics.drawString(FONT, this.placeholderText, x, y, this.placeholderColor);
			}
		}
	}
}
