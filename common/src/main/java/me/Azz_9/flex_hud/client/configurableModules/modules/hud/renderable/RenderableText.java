package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class RenderableText extends Renderable {
	private Text text;
	private int textColor;
	private boolean shadow;

	public RenderableText(int x, int y, Text text, int textColor, boolean shadow) {
		super(x, y);
		this.text = text;
		this.textColor = textColor;
		this.shadow = shadow;
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, textColor, shadow);
	}
}
