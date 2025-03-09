package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.modMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PlaytimeOverlay extends HudElement {
	public boolean showPrefix = true;

	public PlaytimeOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden) {
			return;
		}

		String elapsedTime = getElapsedTime();
		if (showPrefix) {
			elapsedTime = Text.translatable("better_hud.hud.playtime.prefix").getString() + ": " + elapsedTime;
		}

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(this.x, this.y, 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.drawText(CLIENT.textRenderer, elapsedTime, 0, 0, this.color, this.shadow);

		matrices.pop();

		setWidth(elapsedTime);
		this.height = CLIENT.textRenderer.fontHeight;
	}

	private String getElapsedTime() {
		long elapsedMillis = System.currentTimeMillis() - Better_hudClient.getLaunchTime();

		long seconds = (elapsedMillis / 1000) % 60;
		long minutes = (elapsedMillis / (1000 * 60)) % 60;
		long hours = (elapsedMillis / (1000 * 60 * 60));

		return String.format("%1d:%02d:%02d", hours, minutes, seconds);
	}

}
//TODO ajouter option de personnalisation pour l'affichage