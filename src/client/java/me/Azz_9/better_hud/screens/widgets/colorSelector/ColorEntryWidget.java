package me.Azz_9.better_hud.screens.widgets.colorSelector;

import me.Azz_9.better_hud.screens.widgets.buttons.ColorButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ColorEntryWidget extends TextFieldWidget {
	private final ColorButtonWidget colorButtonWidget;
	private final GradientWidget gradientWidget;
	private final HueBarWidget hueBarWidget;

	public ColorEntryWidget(TextRenderer textRenderer, int x, int y, int width, int height, ColorButtonWidget colorButtonWidget, GradientWidget gradientWidget, HueBarWidget hueBarWidget) {
		super(textRenderer, x, y, width, height, Text.literal("Color Entry"));
		this.colorButtonWidget = colorButtonWidget;
		this.gradientWidget = gradientWidget;
		this.hueBarWidget = hueBarWidget;

		setText("#" + Integer.toHexString(colorButtonWidget.getColor()));

		setTextPredicate(text -> text.matches("^#[0-9a-fA-F]{0,6}$"));
	}



	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.isNarratable() && this.isFocused()) {
			if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
				int color = Integer.parseInt(getText().substring(1), 16);

				// Update the GradientWidget and HueBarWidget
				gradientWidget.setColor(color);
				hueBarWidget.setHue(color);
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

	public ColorButtonWidget getColorButtonWidget() {
		return colorButtonWidget;
	}
}