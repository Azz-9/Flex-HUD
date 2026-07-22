package me.Azz_9.flex_hud.client.modules.hud.renderable;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

public class RenderableImage extends Renderable {
	@NotNull
	private final ResourceLocation image;
	private final int width, height;

	public RenderableImage(int x, int y, @NotNull ResourceLocation image, int width, int height) {
		super(x, y, width);
		this.image = image;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, image, x, y, width, height);
	}
}
