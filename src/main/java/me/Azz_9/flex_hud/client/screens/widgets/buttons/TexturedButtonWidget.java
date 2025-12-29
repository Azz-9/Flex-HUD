package me.Azz_9.flex_hud.client.screens.widgets.buttons;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class TexturedButtonWidget extends ImageButton {
	private final int TEXTURE_WIDTH;
	private final int TEXTURE_HEIGHT;

	public TexturedButtonWidget(int x, int y, int width, int height, WidgetSprites sprites, Button.OnPress onPress, int textureWidth, int textureHeight) {
		super(x, y, width, height, sprites, onPress);
		this.TEXTURE_WIDTH = textureWidth;
		this.TEXTURE_HEIGHT = textureHeight;
	}

	public TexturedButtonWidget(int x, int y, int width, int height, WidgetSprites sprites, Button.OnPress onPress) {
		this(x, y, width, height, sprites, onPress, width, height);
	}

	public TexturedButtonWidget(int width, int height, WidgetSprites sprites, Button.OnPress onPress, int textureWidth, int textureHeight) {
		this(0, 0, width, height, sprites, onPress, textureWidth, textureHeight);
	}

	public TexturedButtonWidget(int width, int height, WidgetSprites sprites, Button.OnPress onPress) {
		this(0, 0, width, height, sprites, onPress, width, height);
	}

	@Override
	public boolean isHoveredOrFocused() {
		return this.isFocused();
	}

	@Override
	public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		Identifier identifier = this.sprites.get(this.isActive(), this.isHovered() && this.active);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), 0, 0, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}
}