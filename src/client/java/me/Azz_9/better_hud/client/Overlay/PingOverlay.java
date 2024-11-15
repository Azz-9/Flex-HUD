package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;

public class PingOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        ModConfig modConfigInstance = ModConfig.getInstance();

        if (modConfigInstance.isEnabled && modConfigInstance.showPing) {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && !client.options.hudHidden) {

                if (client.getCurrentServerEntry() != null) {

                    PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
                    if (entry != null) {
                        int latency = entry.getLatency();

                        drawContext.drawText(client.textRenderer, latency + " ms", modConfigInstance.pingHudX, modConfigInstance.pingHudY, modConfigInstance.pingColor, modConfigInstance.pingShadow);
                    }

                } else if (!modConfigInstance.hidePingWhenOffline) {

                    drawContext.drawText(client.textRenderer, "Offline", modConfigInstance.pingHudX, modConfigInstance.pingHudY, modConfigInstance.pingColor, modConfigInstance.pingShadow);

                }

            }

        }

    }

}