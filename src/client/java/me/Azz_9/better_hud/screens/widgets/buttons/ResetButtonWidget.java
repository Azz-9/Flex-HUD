package me.Azz_9.better_hud.screens.widgets.buttons;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class ResetButtonWidget extends TexturedButtonWidget {

	public ResetButtonWidget(int x, int y, int width, int height, PressAction pressAction) {
		super(x, y, width, height, new ButtonTextures(
				Identifier.of(MOD_ID, "widgets/buttons/reset/unfocused.png"),
				Identifier.of(MOD_ID, "widgets/buttons/reset/focused.png")
		), pressAction);
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		Identifier identifier = this.textures.get(this.isNarratable(), this.isSelected() && this.active);
		context.drawTexture(RenderLayer::getGuiTextured, identifier, this.getX(), this.getY(), 0, 0, this.width, this.height, 20, 20);
	}
}
