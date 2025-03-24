package me.Azz_9.better_hud.screens.widgets.buttons;

import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;

public class DependentCustomToggleButtonWidget extends CustomToggleButtonWidget {
	private final CustomToggleButtonWidget DEPENDENCY_BUTTON;
	private final boolean Disable_IF;

	public DependentCustomToggleButtonWidget(int width, int height, boolean toggled, Consumer<Boolean> onToggle, CustomToggleButtonWidget dependencyButton, boolean disableIf) {
		super(width, height, toggled, onToggle);
		this.DEPENDENCY_BUTTON = dependencyButton;
		this.Disable_IF = disableIf;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		this.active = !(DEPENDENCY_BUTTON.isToggled() == Disable_IF);

		super.renderWidget(context, mouseX, mouseY, delta);

		if (DEPENDENCY_BUTTON.isToggled() == Disable_IF) {
			context.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}
}
