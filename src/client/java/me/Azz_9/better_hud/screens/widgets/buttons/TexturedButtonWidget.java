package me.Azz_9.better_hud.screens.widgets.buttons;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class TexturedButtonWidget extends net.minecraft.client.gui.widget.TexturedButtonWidget {
	public TexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures, PressAction pressAction) {
		super(x, y, width, height, textures, pressAction);
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		Identifier identifier = this.textures.get(this.isNarratable(), this.isSelected() && this.active);
		context.drawTexture(RenderLayer::getGuiTextured, identifier, this.getX(), this.getY(), 0, 0, this.width, this.height, 20, 20);
	}
}
