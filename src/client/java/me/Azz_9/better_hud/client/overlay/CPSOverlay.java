package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.utils.CalculateCps;
import me.Azz_9.better_hud.modMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

public class CPSOverlay extends HudElement {
	public boolean showLeftClick = true;
	public boolean showRightClick = true;

	public CPSOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.onHudRender(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || !(this.showLeftClick || this.showRightClick) || CLIENT == null || CLIENT.options.hudHidden) {
			return;
		}

		String text = "";
		if (this.showLeftClick) {
			text = String.valueOf(CalculateCps.getInstance().getLeftCps());
		}
		if (this.showLeftClick && this.showRightClick) {
			text += " | ";
		}
		if (this.showRightClick) {
			text += String.valueOf(CalculateCps.getInstance().getRightCps());
		}

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.drawText(CLIENT.textRenderer, text, 0, 0, this.color, this.shadow);

		matrices.pop();

		setWidth(text);
		this.height = CLIENT.textRenderer.fontHeight;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled && (this.showLeftClick || this.showRightClick);
	}
}
