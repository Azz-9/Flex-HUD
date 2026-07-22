package me.Azz_9.flex_hud.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.Azz_9.flex_hud.client.gui.components.config.Popup;

public abstract class AbstractPopupScreen extends AbstractBackNavigableScreen {

	private @Nullable Popup popupWidget;

	protected AbstractPopupScreen(@NotNull Component title, @Nullable Screen parent) {
		super(title, parent);
	}

	protected AbstractPopupScreen(@NotNull Component title) {
		super(title);
	}

	public void setPopupWidget(@Nullable Popup popupWidget) {
		this.popupWidget = popupWidget;
	}

	public void closePopup() {
		if (popupWidget != null) popupWidget.onClose();
		setPopupWidget(null);
	}

	@Override
	public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		// Suppress hover / tooltips on everything below the overlay
		int mx = popupWidget != null ? -1 : mouseX;
		int my = popupWidget != null ? -1 : mouseY;

		renderBeforeOtherElements(graphics, mouseX, mouseY, deltaTicks);

		super.render(graphics, mx, my, deltaTicks);

		renderBeforePopup(graphics, mx, my, deltaTicks);

		if (popupWidget != null) {
			popupWidget.render(graphics, mouseX, mouseY, deltaTicks);
		}
	}

	public void renderBeforePopup(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
	}

	public void renderBeforeOtherElements(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (popupWidget != null) return popupWidget.mouseClicked(mouseX, mouseY, button);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (popupWidget != null) return popupWidget.mouseReleased(mouseX, mouseY, button);
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (popupWidget != null) return popupWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double hAmount, double vAmount) {
		if (popupWidget != null) return popupWidget.mouseScrolled(mouseX, mouseY, hAmount, vAmount);
		return super.mouseScrolled(mouseX, mouseY, hAmount, vAmount);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (popupWidget != null) {
			if (keyCode == InputConstants.KEY_ESCAPE) {
				closePopup();
				return true;
			}
			return popupWidget.keyPressed(keyCode, scanCode, modifiers);
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (popupWidget != null) return popupWidget.charTyped(codePoint, modifiers);
		return super.charTyped(codePoint, modifiers);
	}
}
