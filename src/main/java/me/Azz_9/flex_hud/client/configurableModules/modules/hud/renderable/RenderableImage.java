package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class RenderableImage extends Renderable {
	private Identifier image;
	private int width, height;

	public RenderableImage(int x, int y, Identifier image, int width, int height) {
		super(x, y);
		this.image = image;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, image, x, y, width, height);
	}
}
