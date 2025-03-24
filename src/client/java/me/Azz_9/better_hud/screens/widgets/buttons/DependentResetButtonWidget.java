package me.Azz_9.better_hud.screens.widgets.buttons;

import net.minecraft.client.gui.DrawContext;

public class DependentResetButtonWidget extends ResetButtonWidget {
	private final CustomToggleButtonWidget DEPENDENCY_BUTTON;
	private final boolean DISABLE_IF;

	public DependentResetButtonWidget(int width, int height, PressAction pressAction, CustomToggleButtonWidget dependencyButton, boolean disableIf) {
		super(width, height, pressAction);
		this.DEPENDENCY_BUTTON = dependencyButton;
		this.DISABLE_IF = disableIf;
	}


	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		this.active = !(DEPENDENCY_BUTTON.isToggled() == DISABLE_IF);

		super.renderWidget(context, mouseX, mouseY, delta);

		if (DEPENDENCY_BUTTON.isToggled() == DISABLE_IF) {
			context.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}
}
