package me.Azz_9.flex_hud.client.screens.widgets.buttons;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class IconButton extends Button {

	private Identifier texture;
	private final int textureWidth;
	private final int textureHeight;

	public IconButton(int x, int y, int width, int height, Identifier texture, int textureWidth, int textureHeight, Button.OnPress onPress) {
		super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
		this.texture = texture;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@Override
	protected void renderContents(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		super.renderDefaultSprite(graphics);

		int iconX = this.getX() + (this.width - textureWidth) / 2;
		int iconY = this.getY() + (this.height - textureHeight) / 2;
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, iconX, iconY, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
	}

	public void setTexture(Identifier texture) {
		this.texture = texture;
	}
}
