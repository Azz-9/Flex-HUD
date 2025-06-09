package me.Azz_9.better_hud.client.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.util.math.MathHelper;

public class AbstractSmoothScrollableList<E extends ElementListWidget.Entry<E>> extends ElementListWidget<E> {
	private double targetScroll;     // Target scroll amount (set by mouse wheel)
	private double currentScroll;    // Interpolated scroll amount (used for rendering)
	private final double SCROLL_SPEED = 25.0; // Pixels per notch
	private final double SCROLL_EASING = 0.25; // Interpolation factor

	public AbstractSmoothScrollableList(MinecraftClient minecraftClient, int width, int height, int y, int itemHeight) {
		super(minecraftClient, width, height, y, itemHeight);
		this.targetScroll = 0.0;
		this.currentScroll = 0.0;
	}

	public AbstractSmoothScrollableList(MinecraftClient minecraftClient, int width, int height, int y, int itemHeight, int headerHeight) {
		super(minecraftClient, width, height, y, itemHeight, headerHeight);
		this.targetScroll = 0.0;
		this.currentScroll = 0.0;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		// Update the target scroll position
		targetScroll -= verticalAmount * SCROLL_SPEED;
		targetScroll = MathHelper.clamp(targetScroll, 0.0F, this.getMaxScrollY() + 1);
		return true;
	}

	@Override
	protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
		// Smoothly interpolate scroll position
		currentScroll = MathHelper.lerp(SCROLL_EASING, currentScroll, targetScroll);

		// Update scrollAmount used by ElementListWidget
		super.setScrollY(currentScroll);

		super.renderList(context, mouseX, mouseY, delta);
	}

	@Override
	public void setScrollY(double scrollY) {
		super.setScrollY(scrollY);
		targetScroll = scrollY;
		currentScroll = scrollY;
	}
}
