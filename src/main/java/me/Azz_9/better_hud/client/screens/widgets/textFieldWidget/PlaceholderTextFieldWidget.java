package me.Azz_9.better_hud.client.screens.widgets.textFieldWidget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class PlaceholderTextFieldWidget extends TextFieldWidget {

	private int placeholderColor = 0xa0a0a0;
	private Text placeholderText;
	private final TextRenderer TEXT_RENDERER;

	public PlaceholderTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
		super(textRenderer, x, y, width, height, text);
		this.TEXT_RENDERER = textRenderer;
	}

	public void setPlaceholderColor(int color) {
		this.placeholderColor = color;
	}

	public void setPlaceholder(Text placeholder) {
		this.placeholderText = placeholder;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.isVisible()) {
			super.renderWidget(context, mouseX, mouseY, delta);

			if (this.placeholderText != null && this.getText().isEmpty()) {
				int x = this.drawsBackground() ? this.getX() + 4 : this.getX();
				int y = this.drawsBackground() ? this.getY() + (this.height - 8) / 2 : this.getY();

				context.drawTextWithShadow(this.TEXT_RENDERER, this.placeholderText, x, y, this.placeholderColor);
			}
		}
	}
}
