package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

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
        // Accéder au gestionnaire de mémoire de la JVM
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        // Obtenir les informations sur la mémoire heap
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

        // Mémoire utilisée et maximum allouée
        long usedMemory = heapMemoryUsage.getUsed();
        long maxMemory = heapMemoryUsage.getMax();

        // Calculer le pourcentage
        return (int) ((double) usedMemory / maxMemory * 100);
    }

}