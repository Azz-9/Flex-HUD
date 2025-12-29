package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector;

import me.Azz_9.flex_hud.client.screens.widgets.textFieldWidget.FilteredEditBox;
import me.Azz_9.flex_hud.client.utils.FlexHudLogger;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class ColorFieldWidget extends FilteredEditBox {

	@NotNull
	private final ColorUpdatable colorSelector;

	ColorFieldWidget(Font font, int width, int height, @NotNull ColorUpdatable colorSelector) {
		super(font, width, height, Component.translatable("flex_hud.color_entry_widget"));
		this.colorSelector = colorSelector;

		setValue("#FFFFFF");

		setFilter(text -> text.matches("^#[0-9a-fA-F]{0,6}$"));

		this.setResponder(text -> this.colorSelector.onUpdateColor(ColorSelector.ColorSelectorElement.COLOR_FIELD));
	}

	@Override
	public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
		if (this.active && this.visible && this.isValidClickButton(click.buttonInfo())) {
			setFocused(this.isMouseOver(click.x(), click.y()));
		}
		return super.mouseClicked(click, doubled);
	}

	public void updateColor(int color) {
		setValue(String.format("#%06X", (0xFFFFFF & color)));
	}

	public int getColor() {
		try {
			if (getValue().length() > 1) {
				return Integer.parseInt(getValue().substring(1), 16);
			}
		} catch (NumberFormatException e) {
			FlexHudLogger.error("Invalid color code: {}", getValue());
		}
		return 0;
	}
}
