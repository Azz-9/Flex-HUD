package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public abstract class Renderable {
	protected int x;
	protected int y;
	protected int width;

	public Renderable(int x, int y, int width) {
		this.x = x;
		this.y = y;
		this.width = width;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public abstract void render(GuiGraphics graphics, DeltaTracker deltaTracker);
}
