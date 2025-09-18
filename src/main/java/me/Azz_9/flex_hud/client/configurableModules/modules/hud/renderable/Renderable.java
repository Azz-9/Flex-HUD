package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public abstract class Renderable {
	protected int x;
	protected int y;

	public Renderable(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public abstract void render(DrawContext context, RenderTickCounter tickCounter);
}
