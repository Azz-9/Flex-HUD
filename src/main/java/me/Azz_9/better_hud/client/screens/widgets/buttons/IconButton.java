package me.Azz_9.better_hud.client.screens.widgets.buttons;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButton extends ButtonWidget {

	private Identifier texture;
	private final int textureWidth;
	private final int textureHeight;

	public IconButton(int x, int y, int width, int height, Identifier texture, int textureWidth, int textureHeight, PressAction onPress) {
		super(x, y, width, height, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
		this.texture = texture;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.renderWidget(context, mouseX, mouseY, deltaTicks);

		int iconX = this.getX() + (this.width - textureWidth) / 2;
		int iconY = this.getY() + (this.height - textureHeight) / 2;
		context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, iconX, iconY, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
	}

	public void setTexture(Identifier texture) {
		this.texture = texture;
	}
}
