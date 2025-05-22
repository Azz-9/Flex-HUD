package me.Azz_9.better_hud.client.screens.widgets.buttons;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;

public class TexturedButtonWidget extends net.minecraft.client.gui.widget.TexturedButtonWidget {
	private final int TEXTURE_WIDTH;
	private final int TEXTURE_HEIGHT;

	public TexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures, PressAction pressAction, int textureWidth, int textureHeight) {
		super(x, y, width, height, textures, pressAction);
		this.TEXTURE_WIDTH = textureWidth;
		this.TEXTURE_HEIGHT = textureHeight;
	}

	public TexturedButtonWidget(int width, int height, ButtonTextures textures, PressAction pressAction, int textureWidth, int textureHeight) {
		super(width, height, textures, pressAction, ScreenTexts.EMPTY);
		this.TEXTURE_WIDTH = textureWidth;
		this.TEXTURE_HEIGHT = textureHeight;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		Identifier identifier = this.textures.get(this.isNarratable(), this.isSelected() && this.active);
		context.drawTexture(RenderLayer::getGuiTextured, identifier, this.getX(), this.getY(), 0, 0, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}
}