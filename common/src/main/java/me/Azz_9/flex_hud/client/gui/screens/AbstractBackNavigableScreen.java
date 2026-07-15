package me.Azz_9.flex_hud.client.gui.screens;


import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class
AbstractBackNavigableScreen extends Screen {
	protected final @Nullable Screen PARENT;

	protected AbstractBackNavigableScreen(@NotNull final Component title, @Nullable final Screen parent) {
		super(title);
		this.PARENT = parent;
	}

	public AbstractBackNavigableScreen(@NotNull final Component title) {
		this(title, null);
	}

	@Override
	public void onClose() {
		if (PARENT != null) {
			minecraft.gui.setScreen(PARENT);
		} else {
			super.onClose();
		}
	}
}
