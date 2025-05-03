package me.Azz_9.better_hud.screens.widgets.buttons;

import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;

public class DependentColorButtonWidget extends ColorButtonWidget implements Dependents {
	private final boolean Disable_IF;
	private boolean isDarked;

	public DependentColorButtonWidget(int width, int height, int currentColor, PressAction onPress, int screenWidth, int screenHeight, Consumer<Integer> consumer, boolean disableIf) {
		super(width, height, currentColor, onPress, screenWidth, screenHeight, consumer);
		this.Disable_IF = disableIf;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		super.renderWidget(context, mouseX, mouseY, delta);

		if (isDarked) {
			context.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	@Override
	public void onDependencyChange(boolean b) {
		this.active = !(b == Disable_IF);
		isDarked = !this.active;
	}
}
