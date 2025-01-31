package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ShriekerWarningLevelOverlay implements HudRenderCallback {

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

		ModConfig INSTANCE = ModConfig.getInstance();
		MinecraftClient client = MinecraftClient.getInstance();

		if (!INSTANCE.isEnabled || !INSTANCE.showShriekerWarningLevel || client == null || client.player == null || client.world == null || client.options.hudHidden) {
			return;
		}

		PlayerEntity player = client.player;

		int warningLevel = 0;
		if (!INSTANCE.showWhenInDeepDark || client.world.getBiome(player.getBlockPos()).getIdAsString().equals("minecraft:deep_dark")) {
			player.sendMessage(Text.of(String.valueOf(player.getSculkShriekerWarningManager().isPresent())), true);
			if (player.getSculkShriekerWarningManager().isPresent()) {
				warningLevel = player.getSculkShriekerWarningManager().get().getWarningLevel();
			}
			drawContext.drawText(client.textRenderer, "Warning level : " + warningLevel, INSTANCE.shriekerWarningLevelHudX, INSTANCE.shriekerWarningLevelHudY, INSTANCE.shriekerWarningLevelColor, INSTANCE.shriekerWarningLevelShadow);
		}
	}
}
