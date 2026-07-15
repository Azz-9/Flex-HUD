package me.Azz_9.flex_hud.client.gui.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPopupScreen extends AbstractBackNavigableScreen {

	private @Nullable AbstractWidget popupWidget;

	protected AbstractPopupScreen(@NotNull Component title, @Nullable Screen parent) {
		super(title, parent);
	}

	protected AbstractPopupScreen(@NotNull Component title) {
		super(title);
	}

	public void setPopupWidget(@Nullable AbstractWidget popupWidget) {
		this.popupWidget = popupWidget;
	}

	@Override
	public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		// Suppress hover / tooltips on everything below the overlay
		int mx = popupWidget != null ? -1 : mouseX;
		int my = popupWidget != null ? -1 : mouseY;

		renderBeforeOtherElements(graphics, mouseX, mouseY, deltaTicks);

		super.extractRenderState(graphics, mx, my, deltaTicks);

		renderBeforePopup(graphics, mx, my, deltaTicks);

		if (popupWidget != null) {
			popupWidget.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
		}
	}

	public void renderBeforePopup(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
	}

	public void renderBeforeOtherElements(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
	}

	@Override
	public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
		if (popupWidget != null) return popupWidget.mouseClicked(event, doubleClick);
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseReleased(@NotNull MouseButtonEvent event) {
		if (popupWidget != null) return popupWidget.mouseReleased(event);
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseDragged(@NotNull MouseButtonEvent event, double dx, double dy) {
		if (popupWidget != null) return popupWidget.mouseDragged(event, dx, dy);
		return super.mouseDragged(event, dx, dy);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double hAmount, double vAmount) {
		if (popupWidget != null) return popupWidget.mouseScrolled(mouseX, mouseY, hAmount, vAmount);
		return super.mouseScrolled(mouseX, mouseY, hAmount, vAmount);
	}

	@Override
	public boolean keyPressed(@NotNull KeyEvent event) {
		if (popupWidget != null) {
			if (event.isEscape()) {
				setPopupWidget(null);
				return true;
			}
			return popupWidget.keyPressed(event);
		}
		return super.keyPressed(event);
	}

	@Override
	public boolean charTyped(@NotNull CharacterEvent event) {
		if (popupWidget != null) return popupWidget.charTyped(event);
		return super.charTyped(event);
	}
}
