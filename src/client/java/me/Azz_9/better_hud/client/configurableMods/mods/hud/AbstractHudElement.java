package me.Azz_9.better_hud.client.configurableMods.mods.hud;

import me.Azz_9.better_hud.client.configurableMods.mods.Mod;
import me.Azz_9.better_hud.client.utils.ChromaColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public abstract class AbstractHudElement extends Mod {
	protected transient final int BACKGROUND_PADDING = 2;

	public double x, y;
	public boolean chromaColor = false;
	public int color = 0xFFFFFF;
	public boolean drawBackground = false;
	public int backgroundColor = 0x313131;
	public boolean shadow = true;
	public float scale = 1.0f;

	protected transient int height, width;
	protected transient double vw, vh;

	public AbstractHudElement(double defaultX, double defaultY) {
		this.x = defaultX;
		this.y = defaultY;
	}

	public void render(DrawContext context, RenderTickCounter tickCounter) {
		this.height = 0;
		this.width = 0;
		vw = MinecraftClient.getInstance().getWindow().getScaledWidth() / 100.0F;
		vh = MinecraftClient.getInstance().getWindow().getScaledHeight() / 100.0F;
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

	protected void setWidth(String text) {
		this.width = MinecraftClient.getInstance().textRenderer.getWidth(text);
	}

	public void setPos(double x, double y) {
		this.x = x;
		this.y = y;
	}

	protected int getColor() {
		if (chromaColor) {
			return ChromaColorUtil.getColor();
		}
		return color;
	}
}
