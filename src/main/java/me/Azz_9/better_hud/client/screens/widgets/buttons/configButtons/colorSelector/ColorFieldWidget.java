package me.Azz_9.better_hud.client.screens.widgets.buttons.configButtons.colorSelector;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ColorFieldWidget extends TextFieldWidget {

	private ColorUpdatable colorSelector;

	ColorFieldWidget(TextRenderer textRenderer, int width, int height, ColorUpdatable colorSelector) {
		super(textRenderer, width, height, Text.translatable("better_hud.color_entry_widget"));
		this.colorSelector = colorSelector;
	}


}
