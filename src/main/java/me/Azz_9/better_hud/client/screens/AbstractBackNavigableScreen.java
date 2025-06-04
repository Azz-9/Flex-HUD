package me.Azz_9.better_hud.client.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class AbstractBackNavigableScreen extends Screen {
	protected final Screen PARENT;

	protected AbstractBackNavigableScreen(Text title, Screen parent) {
		super(title);
		this.PARENT = parent;
	}

	@Override
	public void close() {
		if (client != null && PARENT != null) {
			client.setScreen(PARENT);
		} else {
			super.close();
		}
	}
}
