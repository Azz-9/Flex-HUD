package me.Azz_9.flex_hud.client.screens;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractSmoothScrollableList<E extends ElementListWidget.Entry<E>> extends ElementListWidget<E> {
	private double targetScroll = 0; // Target scroll amount (set by mouse wheel)
	private double currentScroll = 0; // Interpolated scroll amount (used for rendering)
	private final double SCROLL_SPEED = 25.0; // Pixels per notch
	private long lastUpdateTime = System.nanoTime();

	private final boolean externalSmoothDetected = FabricLoader.getInstance().isModLoaded("smoothscroll") ||
			FabricLoader.getInstance().isModLoaded("smoothscrollingrefurbished");

	public AbstractSmoothScrollableList(MinecraftClient minecraftClient, int width, int height, int y, int itemHeight) {
		super(minecraftClient, width, height, y, itemHeight);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (externalSmoothDetected) {
			return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}

		// Update the target scroll position
		targetScroll -= verticalAmount * SCROLL_SPEED;
		targetScroll = MathHelper.clamp(targetScroll, 0.0F, this.getMaxScrollY() + 1);
		return true;
	}

	@Override
	protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
		if (externalSmoothDetected) {
			super.renderList(context, mouseX, mouseY, delta);
			return;
		}

		long currentTime = System.nanoTime();
		double deltaSeconds = (currentTime - lastUpdateTime) / 1_000_000_000.0; // Convertir en secondes
		lastUpdateTime = currentTime;

		double alpha = 1.0 - Math.exp(-SCROLL_SPEED * deltaSeconds);

		currentScroll += (targetScroll - currentScroll) * alpha;

		super.setScrollY(currentScroll);
		super.renderList(context, mouseX, mouseY, delta);
	}

	@Override
	public void setScrollY(double scrollY) {
		if (externalSmoothDetected) {
			super.setScrollY(scrollY);
			return;
		}

		super.setScrollY(scrollY);
		targetScroll = scrollY;
		currentScroll = scrollY;
	}
}
