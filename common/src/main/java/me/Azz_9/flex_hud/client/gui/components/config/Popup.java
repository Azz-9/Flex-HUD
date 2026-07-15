package me.Azz_9.flex_hud.client.gui.components.config;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

public interface Popup extends Renderable, GuiEventListener {

	default void onClose() {
	}
}
