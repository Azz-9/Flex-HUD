package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ColorFieldWidget extends TextFieldWidget {

	private ColorUpdatable colorSelector;
	private boolean suppressTextFieldCallback = false;

	ColorFieldWidget(TextRenderer textRenderer, int width, int height, ColorUpdatable colorSelector) {
		super(textRenderer, width, height, Text.translatable("flex_hud.color_entry_widget"));
		this.colorSelector = colorSelector;

		setText("#FFFFFF");

		setChangedListener(text -> {
			if (suppressTextFieldCallback) return;

			this.colorSelector.onUpdateColor(ColorSelector.ColorSelectorElement.COLOR_FIELD);
		});
		setTextPredicate(text -> text.matches("^#[0-9a-fA-F]{0,6}$"));
	}

	@Override
	public void setText(String text) {
		// do not call the changed listener when using setText() method
		suppressTextFieldCallback = true;
		super.setText(text);
		suppressTextFieldCallback = false;
	}

	@Override
	public boolean mouseClicked(Click click, boolean doubled) {
		if (this.active && this.visible && this.isValidClickButton(click.buttonInfo())) {
			setFocused(this.isMouseOver(click.x(), click.y()));
		}
		return super.mouseClicked(click, doubled);
	}

	public void updateColor(int color) {
		setText(String.format("#%06X", (0xFFFFFF & color)));
	}

	public int getColor() {
		try {
			if (getText().length() > 1) {
				return Integer.parseInt(getText().substring(1), 16);
			}
		} catch (NumberFormatException e) {
			Flex_hudClient.LOGGER.error("Invalid color code: {}", getText());
		}
		return 0;
	}
}
