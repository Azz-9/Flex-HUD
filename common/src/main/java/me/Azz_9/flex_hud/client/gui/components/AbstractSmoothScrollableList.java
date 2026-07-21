package me.Azz_9.flex_hud.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.util.Mth;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.Constants;
import me.Azz_9.flex_hud.platform.Services;

public abstract class AbstractSmoothScrollableList<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {

	private static final double SCROLL_SNAP_DISTANCE = 0.5;
	private double targetScroll = 0; // Target scroll amount (set by mouse wheel)
	private double currentScroll = 0; // Interpolated scroll amount (used for rendering)
	private final double SCROLL_SPEED = 25.0; // Pixels per notch
	private long lastUpdateTime = System.nanoTime();

	private final boolean externalSmoothDetected = Services.PLATFORM.isModLoaded(Constants.SMOOTH_SCROLLING_ID) ||
			Services.PLATFORM.isModLoaded(Constants.SMOOTH_SCROLLING_REFURBISHED_ID);

	public AbstractSmoothScrollableList(Minecraft minecraftClient, int width, int height, int y, int itemHeight) {
		super(minecraftClient, width, height, y, itemHeight);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (externalSmoothDetected) {
			return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}

		// Update the target scroll position
		targetScroll -= verticalAmount * SCROLL_SPEED;
		targetScroll = Mth.clamp(targetScroll, 0.0F, this.maxScrollAmount() + 1);
		return true;
	}

	@Override
	protected void renderListItems(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (externalSmoothDetected) {
			super.renderListItems(graphics, mouseX, mouseY, delta);
			return;
		}

		long currentTime = System.nanoTime();
		double deltaSeconds = (currentTime - lastUpdateTime) / 1_000_000_000.0; // Convertir en secondes
		lastUpdateTime = currentTime;

		double alpha = 1.0 - Math.exp(-SCROLL_SPEED * deltaSeconds);

		currentScroll += (targetScroll - currentScroll) * alpha;
		if (Math.abs(targetScroll - currentScroll) < SCROLL_SNAP_DISTANCE) {
			currentScroll = targetScroll;
		}
		currentScroll = Mth.clamp(currentScroll, 0.0, this.maxScrollAmount());

		super.setScrollAmount(currentScroll);
		super.renderListItems(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void setScrollAmount(double scrollY) {
		if (externalSmoothDetected) {
			super.setScrollAmount(scrollY);
			return;
		}

		super.setScrollAmount(scrollY);
		targetScroll = scrollY;
		currentScroll = scrollY;
	}
}
