package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.modMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MemoryUsageOverlay extends HudElement {

    public MemoryUsageOverlay(double defaultX, double defaultY) {
        super(defaultX, defaultY);
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);
        
        final MinecraftClient CLIENT = MinecraftClient.getInstance();

        if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden) {
            return;
        }

        String text = "Mem: " + getMemoryUsagePercentage() + "%";

        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        matrices.translate(this.x, this.y, 0);
        matrices.scale(this.scale, this.scale, 1.0f);

        drawContext.drawText(CLIENT.textRenderer, text, 0, 0, this.color, this.shadow);

        matrices.pop();

        setWidth(text);
        this.height = CLIENT.textRenderer.fontHeight;
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