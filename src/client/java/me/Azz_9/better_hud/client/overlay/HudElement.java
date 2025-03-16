package me.Azz_9.better_hud.client.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public abstract class HudElement implements HudRenderCallback {
	public boolean enabled = true;
	public int color = 0xFFFFFF;
	public boolean shadow = true;
	public double x;
	public double y;
	public float scale = 1.0f;
	transient protected int height;
	transient protected int width;
	transient protected double vw;
	transient protected double vh;

	public HudElement(double defaultX, double defaultY) {
		this.x = defaultX;
		this.y = defaultY;
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		this.height = 0;
		this.width = 0;
		vw = MinecraftClient.getInstance().getWindow().getScaledWidth() / 100.0F;
		vh = MinecraftClient.getInstance().getWindow().getScaledHeight() / 100.0F;
	}

	protected void setWidth(String text) {
		this.width = MinecraftClient.getInstance().textRenderer.getWidth(text);
	}

	protected void updateWidth(String text) {
		int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(text);
		if (textWidth > this.width) {
			this.width = textWidth;
		}
	}

	protected void updateWidth(String text, int startX) {
		int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(text);
		if (startX + textWidth > this.width) {
			this.width = startX + textWidth;
		}
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public void setPos(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public boolean isEnabled() {
		return this.enabled;
	}
}
