package me.Azz_9.flex_hud.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicSpriteIconButton extends Button {
	private @Nullable ResourceLocation sprite;
	private final int spriteWidth;
	private final int spriteHeight;


	public DynamicSpriteIconButton(int x, int y, int width, int height, @Nullable ResourceLocation sprite, int spriteWidth, int spriteHeight, OnPress onPress) {
		super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
		this.sprite = sprite;
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
	}

	@Override
	protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.renderWidget(graphics, mouseX, mouseY, delta);

		if (sprite == null) return;

		int iconX = this.getX() + (this.width - spriteWidth) / 2;
		int iconY = this.getY() + (this.height - spriteHeight) / 2;
		graphics.blitSprite(RenderType::guiTextured, sprite, iconX, iconY, spriteWidth, spriteHeight);
	}

	@Override
	public void renderString(@NotNull GuiGraphics guiGraphics, @NotNull Font font, int color) {
	}

	public void setSprite(@Nullable ResourceLocation sprite) {
		this.sprite = sprite;
	}
}
