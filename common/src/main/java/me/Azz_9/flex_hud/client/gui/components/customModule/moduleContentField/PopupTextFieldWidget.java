package me.Azz_9.flex_hud.client.gui.components.customModule.moduleContentField;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.network.chat.Component;

import me.Azz_9.flex_hud.client.gui.components.CustomEditBox;

public final class PopupTextFieldWidget extends CustomEditBox {
	PopupTextFieldWidget(int width, int height) {
		super(MINECRAFT.font, 0, 0, width, height, Component.empty());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible && this.isValidClickButton(button)) {
			setFocused(this.isMouseOver(mouseX, mouseY));
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
}