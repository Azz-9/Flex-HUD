package me.Azz_9.flex_hud.client.gui.components.customModule.moduleContentField;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.gui.components.CustomEditBox;

public final class PopupTextFieldWidget extends CustomEditBox {
	PopupTextFieldWidget(int width, int height) {
		super(MINECRAFT.font, 0, 0, width, height, Component.empty());
	}

	@Override
	public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubled) {
		if (this.active && this.visible && this.isValidClickButton(event.buttonInfo())) {
			setFocused(this.isMouseOver(event.x(), event.y()));
		}
		return super.mouseClicked(event, doubled);
	}
}