package me.Azz_9.flex_hud.client.gui.components.config.colorSelector;

import net.minecraft.client.gui.Font;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.components.CustomEditBox;

public class ColorFieldWidget extends CustomEditBox {

	private static final String COLOR_REGEX = "^#[0-9a-fA-F]{0,6}$";

	private static final int VALID_TEXT_COLOR = Colors.WHITE;
	private static final int INVALID_TEXT_COLOR = Colors.RED;

	@NotNull
	private final ColorUpdatable colorSelector;

	ColorFieldWidget(Font font, int width, int height, @NotNull ColorUpdatable colorSelector) {
		super(font, 0, 0, width, height, Component.translatable("flex_hud.color_entry_widget"));
		this.colorSelector = colorSelector;

		setValue("#FFFFFF");

		this.setResponder(text -> {
			if (text.matches(COLOR_REGEX)) {
				setTextColor(VALID_TEXT_COLOR);
				this.colorSelector.onUpdateColor(ColorSelector.ColorSelectorElement.COLOR_FIELD);
			} else {
				setTextColor(INVALID_TEXT_COLOR);
			}
		});
	}

	@Override
	public boolean mouseClicked(@NotNull MouseButtonEvent click, boolean doubled) {
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
		} catch (NumberFormatException ignored) {
		}
		return 0;
	}
}
