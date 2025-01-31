package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.client.Better_hudClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class PlaytimeOverlay extends HudElement {

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

		ModConfig INSTANCE = ModConfig.getInstance();
		MinecraftClient client = MinecraftClient.getInstance();

		if (!INSTANCE.isEnabled || !INSTANCE.showPlaytime || client == null || client.options.hudHidden) {
			return;
		}

		this.x = INSTANCE.playtimeHudX;
		this.y = INSTANCE.playtimeHudY;

		String elapsedTime = getElapsedTime();
		drawContext.drawText(client.textRenderer, elapsedTime, INSTANCE.playtimeHudX, INSTANCE.playtimeHudY, INSTANCE.playtimeColor, INSTANCE.playtimeShadow);

		setWidth(elapsedTime);
		this.height = client.textRenderer.fontHeight;
	}

	public static String getElapsedTime() {
		long elapsedMillis = System.currentTimeMillis() - Better_hudClient.getLaunchTime();

		long seconds = (elapsedMillis / 1000) % 60;
		long minutes = (elapsedMillis / (1000 * 60)) % 60;
		long hours = (elapsedMillis / (1000 * 60 * 60));

		return String.format("%1d:%02d:%02d", hours, minutes, seconds);
	}

	@Override
	public void setPos(int x, int y) {
		ModConfig INSTANCE = ModConfig.getInstance();
		INSTANCE.playtimeHudX = x;
		INSTANCE.playtimeHudY = y;
	}

	@Override
	public boolean isEnabled() {
		return ModConfig.getInstance().showPlaytime;
	}
}
//TODO ajouter option de personnalisation pour l'affichage