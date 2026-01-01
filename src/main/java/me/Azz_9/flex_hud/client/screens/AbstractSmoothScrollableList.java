package me.Azz_9.flex_hud.client.screens;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import org.jspecify.annotations.NonNull;

public abstract class AbstractSmoothScrollableList<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
	private double targetScroll = 0; // Target scroll amount (set by mouse wheel)
	private double currentScroll = 0; // Interpolated scroll amount (used for rendering)
	private final double SCROLL_SPEED = 25.0; // Pixels per notch
	private long lastUpdateTime = System.nanoTime();

	private final boolean externalSmoothDetected = FabricLoader.getInstance().isModLoaded("smoothscroll") ||
			FabricLoader.getInstance().isModLoaded("smoothscrollingrefurbished");

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
		targetScroll = Math.clamp(targetScroll, 0.0F, this.maxScrollAmount() + 1);
		return true;
	}

	@Override
	protected void renderListItems(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (externalSmoothDetected) {
			super.renderListItems(graphics, mouseX, mouseY, delta);
			return;
		}

		long currentTime = System.nanoTime();
		double deltaSeconds = (currentTime - lastUpdateTime) / 1_000_000_000.0; // Convertir en secondes
		lastUpdateTime = currentTime;

		double alpha = 1.0 - Math.exp(-SCROLL_SPEED * deltaSeconds);

		currentScroll += (targetScroll - currentScroll) * alpha;

		super.setScrollAmount(currentScroll);
		super.render(graphics, mouseX, mouseY, delta);
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
