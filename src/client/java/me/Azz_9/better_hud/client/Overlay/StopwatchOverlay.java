package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class StopwatchOverlay implements HudRenderCallback {

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

		MinecraftClient client = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !ModConfig.getInstance().showStopwatch || client == null || client.options.hudHidden) {
			return;
		}




		//TODO

	}
}
