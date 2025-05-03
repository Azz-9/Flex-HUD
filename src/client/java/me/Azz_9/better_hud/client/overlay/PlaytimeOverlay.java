package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.utils.ChromaColor;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.Playtime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PlaytimeOverlay extends HudElement {
	public boolean showPrefix = true;

	public PlaytimeOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
		this.enabled = false; // disable by default
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden) {
			return;
		}

		String elapsedTime = getElapsedTime();
		if (showPrefix) {
			elapsedTime = Text.translatable("better_hud.playtime.hud.prefix").getString() + ": " + elapsedTime;
		}

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.drawText(CLIENT.textRenderer, elapsedTime, 0, 0, (chromaColor ? ChromaColor.getColor() : this.color), this.shadow);

		setWidth(elapsedTime);
		this.height = CLIENT.textRenderer.fontHeight;

		if (drawBackground) {
			drawContext.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor);
		}

		matrices.pop();
	}

	private String getElapsedTime() {
		long elapsedMillis = System.currentTimeMillis() - Better_hudClient.getLaunchTime();

		long seconds = (elapsedMillis / 1000) % 60;
		long minutes = (elapsedMillis / (1000 * 60)) % 60;
		long hours = (elapsedMillis / (1000 * 60 * 60));

		return String.format("%1d:%02d:%02d", hours, minutes, seconds);
	}

	@Override
	public Screen getConfigScreen(Screen parent) {
		return new Playtime(parent, 0);
	}
}
//TODO ajouter option de personnalisation pour l'affichage