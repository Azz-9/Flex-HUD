package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.client.Interface.MovableElementInterface;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudElement implements HudRenderCallback, MovableElementInterface {
	protected int x;
	protected int y;
	protected int height;
	protected int width;

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		this.height = 0;
		this.width = 0;
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

	public int getX() {
		return this.x;
	}

	public int getY() {
        return this.y;
    }


	@Override
	public void setScale(float scale) {
	}

	@Override
	public void setPos(int x, int y) {
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}
