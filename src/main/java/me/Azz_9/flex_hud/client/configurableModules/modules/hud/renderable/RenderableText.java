package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

public class RenderableText extends Renderable {
	@NotNull
	private final Component text;
	private final int textColor;
	private final boolean shadow;

	public RenderableText(int x, int y, @NotNull Component text, int textColor, boolean shadow) {
		super(x, y, MINECRAFT.font.width(text.getString()));
		this.text = text;
		this.textColor = textColor;
		this.shadow = shadow;
	}

	@Override
	public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		if (!text.getString().isBlank()) {
			graphics.text(MINECRAFT.font, text, x, y, textColor, shadow);
		}
	}
}
