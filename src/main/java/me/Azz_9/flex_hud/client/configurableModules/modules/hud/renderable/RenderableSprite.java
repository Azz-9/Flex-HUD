package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import org.jetbrains.annotations.NotNull;

public class RenderableSprite extends Renderable {
	@NotNull
	private final Sprite sprite;
	private final int width, height;

	public RenderableSprite(int x, int y, @NotNull Sprite sprite, int width, int height) {
		super(x, y, width);
		this.sprite = sprite;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		context.drawSpriteStretched(RenderLayer::getGuiTextured, sprite, x, y, width, height);
	}
}
