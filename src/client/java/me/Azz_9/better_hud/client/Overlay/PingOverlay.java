package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.client.Better_hudClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;

public class PingOverlay extends HudElement {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        ModConfig INSTANCE = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!INSTANCE.isEnabled || !INSTANCE.showPing || client == null || client.options.hudHidden || client.player == null) {
            return;
        }

        this.x = INSTANCE.pingHudX;
        this.y = INSTANCE.pingHudY;

        String text = "";

        if (client.getCurrentServerEntry() != null) {

            if (client.getNetworkHandler() != null) {
                PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());

                if (entry != null) {
                    int latency = entry.getLatency();

                    text = latency + " ms";
                    drawContext.drawText(client.textRenderer, text, INSTANCE.pingHudX, INSTANCE.pingHudY, INSTANCE.pingColor, INSTANCE.pingShadow);
                }
            }

        } else if (!INSTANCE.hidePingWhenOffline) {

            text = "Offline";
            drawContext.drawText(client.textRenderer, text, INSTANCE.pingHudX, INSTANCE.pingHudY, INSTANCE.pingColor, INSTANCE.pingShadow);

        } else if (Better_hudClient.isEditing) {

            text = "20 ms";
            drawContext.drawText(client.textRenderer, text, INSTANCE.pingHudX, INSTANCE.pingHudY, INSTANCE.pingColor, INSTANCE.pingShadow);

        }

        if (!text.isEmpty()) {
            setWidth(text);
            this.height = client.textRenderer.fontHeight;
        }
    }

    @Override
    public void setPos(int x, int y) {
        ModConfig INSTANCE = ModConfig.getInstance();
        INSTANCE.pingHudX = x;
        INSTANCE.pingHudY = y;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.getInstance().showPing;
    }

}