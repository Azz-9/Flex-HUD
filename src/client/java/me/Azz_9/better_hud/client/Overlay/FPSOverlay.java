package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class FPSOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        ModConfig modConfigInstance = ModConfig.getInstance();

        if (modConfigInstance.isEnabled && modConfigInstance.showFPS) {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && !client.options.hudHidden) {

                drawContext.drawText(client.textRenderer, client.getCurrentFps() + " FPS", modConfigInstance.FPSHudX, modConfigInstance.FPSHudY, modConfigInstance.FPSColor, modConfigInstance.FPSShadow);

            }

        }

    }

}
