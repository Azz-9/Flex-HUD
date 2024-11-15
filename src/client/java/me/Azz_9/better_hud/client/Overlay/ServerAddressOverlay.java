package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class ServerAddressOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        ModConfig modConfigInstance = ModConfig.getInstance();

        if (modConfigInstance.isEnabled && modConfigInstance.showServerAddress) {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && !client.options.hudHidden) {

                if (client.getCurrentServerEntry() != null) {

                    String address = client.getCurrentServerEntry().address;

                    drawContext.drawText(client.textRenderer, address, modConfigInstance.serverAddressHudX, modConfigInstance.serverAddressHudY, modConfigInstance.serverAddressColor, modConfigInstance.serverAddressShadow);

                } else if (!modConfigInstance.hideServerAddressWhenOffline) {

                    drawContext.drawText(client.textRenderer, "Offline", modConfigInstance.serverAddressHudX, modConfigInstance.serverAddressHudY, modConfigInstance.serverAddressColor, modConfigInstance.serverAddressShadow);

                }

            }

        }

    }

}
