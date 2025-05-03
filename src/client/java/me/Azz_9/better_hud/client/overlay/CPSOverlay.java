package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.utils.CalculateCps;
import me.Azz_9.better_hud.client.utils.ChromaColor;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.CPS;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

public class CPSOverlay extends HudElement {
	public boolean showLeftClick = true;
	public boolean showRightClick = true;

	public CPSOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);

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

		drawContext.drawText(CLIENT.textRenderer, text, 0, 0, (chromaColor ? ChromaColor.getColor() : this.color), this.shadow);

		setWidth(text);
		this.height = CLIENT.textRenderer.fontHeight;

		if (drawBackground) {
			drawContext.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor);
		}

		matrices.pop();
	}

	@Override
	public boolean isEnabled() {
		return this.enabled && (this.showLeftClick || this.showRightClick);
	}

	@Override
	public Screen getConfigScreen(Screen parent) {
		return new CPS(parent, 0);
	}
}
