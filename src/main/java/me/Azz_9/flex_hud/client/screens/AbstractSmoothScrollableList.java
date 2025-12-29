package me.Azz_9.flex_hud.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import org.jspecify.annotations.NonNull;

public abstract class AbstractSmoothScrollableList<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
	private double targetScroll;     // Target scroll amount (set by mouse wheel)
	private double currentScroll;    // Interpolated scroll amount (used for rendering)
	private final double SCROLL_SPEED = 25.0; // Pixels per notch
	private long lastUpdateTime = System.nanoTime();

	public AbstractSmoothScrollableList(Minecraft minecraftClient, int width, int height, int y, int itemHeight) {
		super(minecraftClient, width, height, y, itemHeight);
		this.targetScroll = 0.0;
		this.currentScroll = 0.0;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		// Update the target scroll position
		targetScroll -= verticalAmount * SCROLL_SPEED;
		targetScroll = Math.clamp(targetScroll, 0.0F, this.maxScrollAmount() + 1);
		return true;
	}

	@Override
	protected void renderListItems(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float a) {
		long currentTime = System.nanoTime();
		double deltaSeconds = (currentTime - lastUpdateTime) / 1_000_000_000.0; // Convertir en secondes
		lastUpdateTime = currentTime;

		double alpha = 1.0 - Math.exp(-SCROLL_SPEED * deltaSeconds);

		currentScroll += (targetScroll - currentScroll) * alpha;

		super.setScrollAmount(currentScroll);
		super.render(graphics, mouseX, mouseY, a);
	}

	@Override
	public void setScrollAmount(double scrollY) {
		super.setScrollAmount(scrollY);
		targetScroll = scrollY;
		currentScroll = scrollY;
	}


}
