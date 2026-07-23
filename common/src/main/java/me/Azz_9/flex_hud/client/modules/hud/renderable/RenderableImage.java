package me.Azz_9.flex_hud.client.modules.hud.renderable;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

public class RenderableImage extends Renderable {
	@NotNull
	private final ResourceLocation image;
	private final TextureAtlasSprite sprite;
	private final int width, height;

	public RenderableImage(int x, int y, @NotNull ResourceLocation image, int width, int height) {
		super(x, y, width);
		this.image = image;
		this.sprite = null;
		this.width = width;
		this.height = height;
	}

	public RenderableImage(int x, int y, @NotNull TextureAtlasSprite sprite, int width, int height) {
		super(x, y, width);
		this.image = null;
		this.sprite = sprite;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (sprite != null) {
			graphics.blitSprite(RenderType::guiTextured, sprite, x, y, width, height);
		} else {
			graphics.blitSprite(RenderType::guiTextured, image, x, y, width, height);
		}
	}
}
