package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector;

import me.Azz_9.flex_hud.client.utils.FlexHudLogger;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class ColorFieldWidget extends TextFieldWidget {

	@NotNull
	private final ColorUpdatable colorSelector;

	ColorFieldWidget(TextRenderer textRenderer, int width, int height, @NotNull ColorUpdatable colorSelector) {
		super(textRenderer, width, height, Text.translatable("flex_hud.color_entry_widget"));
		this.colorSelector = colorSelector;

		setText("#FFFFFF");

		setTextPredicate(text -> text.matches("^#[0-9a-fA-F]{0,6}$"));

		setChangedListener(text -> this.colorSelector.onUpdateColor(ColorSelector.ColorSelectorElement.COLOR_FIELD));
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
			FlexHudLogger.error("Invalid color code: {}", getText());
		}
		return 0;
	}
}
