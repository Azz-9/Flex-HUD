package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.modMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class StopwatchOverlay extends HudElement {

	public StopwatchOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

		MinecraftClient client = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || client == null || client.options.hudHidden) {
			return;
		}




		//TODO

	}
}
