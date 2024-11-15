package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class MemoryUsageOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        ModConfig modConfigInstance = ModConfig.getInstance();

        if (modConfigInstance.isEnabled && modConfigInstance.showMemoryUsage) {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && !client.options.hudHidden) {

                drawContext.drawText(client.textRenderer, "Mem: " + getMemoryUsagePercentage() + "%", modConfigInstance.memoryUsageHudX, modConfigInstance.memoryUsageHudY, modConfigInstance.memoryUsageColor, modConfigInstance.memoryUsageShadow);

            }

        }

    }

    private int getMemoryUsagePercentage() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();
        long usedMemory = totalMemory - runtime.freeMemory(); // Memory used is allocated memory minus free memory

        return (int) ((usedMemory * 100) / totalMemory);
    }

}
// TODO ça marche pas bien