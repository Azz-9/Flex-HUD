package me.Azz_9.better_hud.client.configurableModules.modules.hud;

import me.Azz_9.better_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.better_hud.client.utils.ChromaColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractHudElement extends AbstractModule implements MovableModule {
	protected transient final int BACKGROUND_PADDING = 2;

	public double offsetX, offsetY;
	@NotNull
	public AnchorPosition anchorX, anchorY;
	public float scale = 1.0f;

	public boolean shadow = true;
	public boolean chromaColor = false;
	public int color = 0xffffff;
	public boolean drawBackground = false;
	public int backgroundColor = 0x313131;
	public boolean hideInF3 = true;

	protected transient int height, width;

	public AbstractHudElement(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		this.offsetX = defaultOffsetX;
		this.offsetY = defaultOffsetY;
		this.anchorX = defaultAnchorX;
		this.anchorY = defaultAnchorY;
	}

	public void init() {
	}

	public abstract void render(DrawContext context, RenderTickCounter tickCounter);

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

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public double getOffsetX() {
		return offsetX;
	}

	@Override
	public double getOffsetY() {
		return offsetY;
	}

	@Override
	public @NotNull AnchorPosition getAnchorX() {
		return anchorX;
	}

	@Override
	public @NotNull AnchorPosition getAnchorY() {
		return anchorY;
	}

	protected void setWidth(String text) {
		this.width = MinecraftClient.getInstance().textRenderer.getWidth(text);
	}

	@Override
	public void setPos(double offsetX, double offsetY, AnchorPosition anchorX, AnchorPosition anchorY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.anchorX = anchorX;
		this.anchorY = anchorY;
	}

	@Override
	public float getScale() {
		return scale;
	}

	@Override
	public void setScale(float scale) {
		this.scale = scale;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	protected int getColor() {
		if (chromaColor) {
			return ChromaColorUtils.getColor();
		}
		return color | 0xff000000;
	}

	public enum AnchorPosition {
		START,
		CENTER,
		END
	}
}
