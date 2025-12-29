package me.Azz_9.flex_hud.client.configurableModules.modules.hud.renderable;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class RenderableText extends Renderable {
	@NotNull
	private final Component text;
	private final int textColor;
	private final boolean shadow;

	public RenderableText(int x, int y, @NotNull Component text, int textColor, boolean shadow) {
		super(x, y, Minecraft.getInstance().font.width(text.getString()));
		this.text = text;
		this.textColor = textColor;
		this.shadow = shadow;
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		if (!text.getString().isBlank()) {
			graphics.drawString(Minecraft.getInstance().font, text, x, y, textColor, shadow);
		}
	}
}
