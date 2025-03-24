package me.Azz_9.better_hud.screens.widgets.buttons;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

public class DependentTextWidget extends TextWidget {
	private final CustomToggleButtonWidget DEPENDENCY_BUTTON;
	private final boolean Disable_IF;

	public DependentTextWidget(Text message, TextRenderer textRenderer, CustomToggleButtonWidget dependencyButton, boolean disableIf) {
		super(message, textRenderer);
		this.DEPENDENCY_BUTTON = dependencyButton;
		this.Disable_IF = disableIf;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		this.active = !(DEPENDENCY_BUTTON.isToggled() == Disable_IF);

		if (DEPENDENCY_BUTTON.isToggled() == Disable_IF) {
			this.setTextColor(0x8a8a8a);
		} else {
			this.setTextColor(0xffffff);
		}

		super.renderWidget(context, mouseX, mouseY, delta);
	}
}
