package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MemoryUsageOverlay extends HudElement {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);

        ModConfig INSTANCE = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!INSTANCE.isEnabled || !INSTANCE.showMemoryUsage || client == null || client.options.hudHidden) {
            return;
        }

        this.x = INSTANCE.memoryUsageHudX;
        this.y = INSTANCE.memoryUsageHudY;

        String text = "Mem: " + getMemoryUsagePercentage() + "%";
        drawContext.drawText(client.textRenderer, text, INSTANCE.memoryUsageHudX, INSTANCE.memoryUsageHudY, INSTANCE.memoryUsageColor, INSTANCE.memoryUsageShadow);

        setWidth(text);
        this.height = client.textRenderer.fontHeight;
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

    @Override
    public void setPos(int x, int y) {
        ModConfig INSTANCE = ModConfig.getInstance();
        INSTANCE.memoryUsageHudX = x;
        INSTANCE.memoryUsageHudY = y;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.getInstance().showMemoryUsage;
    }

}