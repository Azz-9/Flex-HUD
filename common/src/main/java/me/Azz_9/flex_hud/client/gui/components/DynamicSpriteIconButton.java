package me.Azz_9.flex_hud.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicSpriteIconButton extends Button {
	private @Nullable Identifier sprite;
	private final int spriteWidth;
	private final int spriteHeight;


	public DynamicSpriteIconButton(int x, int y, int width, int height, @Nullable Identifier sprite, int spriteWidth, int spriteHeight, OnPress onPress) {
		super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
		this.sprite = sprite;
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
	}

	@Override
	protected void renderContents(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.renderDefaultSprite(graphics);

		if (sprite == null) return;

		int iconX = this.getX() + (this.width - spriteWidth) / 2;
		int iconY = this.getY() + (this.height - spriteHeight) / 2;
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, iconX, iconY, spriteWidth, spriteHeight, alpha);
	}

	public void setSprite(@Nullable Identifier sprite) {
		this.sprite = sprite;
	}
}
