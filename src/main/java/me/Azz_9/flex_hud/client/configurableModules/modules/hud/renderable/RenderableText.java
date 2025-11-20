package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class RenderableText extends Renderable {
	@NotNull
	private final Text text;
	private final int textColor;
	private final boolean shadow;

	public RenderableText(int x, int y, @NotNull Text text, int textColor, boolean shadow) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(text.getString()));
		this.text = text;
		this.textColor = textColor;
		this.shadow = shadow;
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (!text.getString().isBlank()) {
			context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, textColor, shadow);
		}
	}
}
