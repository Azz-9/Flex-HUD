package me.Azz_9.better_hud.screens.widgets.colorSelector;

import me.Azz_9.better_hud.screens.widgets.buttons.ColorButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ColorEntryWidget extends TextFieldWidget {
	private final ColorButtonWidget COLOR_BUTTON_WIDGET;
	private final GradientWidget GRADIENT_WIDGET;
	private final HueBarWidget HUE_BAR_WIDGET;

	public ColorEntryWidget(TextRenderer textRenderer, int width, int height, ColorButtonWidget colorButtonWidget, GradientWidget gradientWidget, HueBarWidget hueBarWidget) {
		super(textRenderer, 0, 0, width, height, Text.translatable("better_hud.color_entry_widget"));
		this.COLOR_BUTTON_WIDGET = colorButtonWidget;
		this.GRADIENT_WIDGET = gradientWidget;
		this.HUE_BAR_WIDGET = hueBarWidget;

		setText("#" + Integer.toHexString(colorButtonWidget.getColor()));

		setTextPredicate(text -> text.matches("^#[0-9a-fA-F]{0,6}$"));
	}


	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.isNarratable() && this.isFocused()) {
			if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
				int color = Integer.parseInt(getText().substring(1), 16);

				// Update the GradientWidget and HueBarWidget
				GRADIENT_WIDGET.setColor(color);
				HUE_BAR_WIDGET.setHue(color);
				return true;
			} else if (Screen.isPaste(keyCode)) {
				String textToPaste = MinecraftClient.getInstance().keyboard.getClipboard();
				if (textToPaste.startsWith("#")) {
					textToPaste = textToPaste.substring(1);
				}
				this.write(textToPaste);
				return true;
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public ColorButtonWidget getCOLOR_BUTTON_WIDGET() {
		return COLOR_BUTTON_WIDGET;
	}
}