package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.Deque;
import java.util.LinkedList;

public class CPSOverlay implements HudRenderCallback {

    private static final Deque<Long> leftClickTimestamps = new LinkedList<>();
    private static final Deque<Long> rightClickTimestamps = new LinkedList<>();

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        ModConfig modConfigInstance = ModConfig.getInstance();

        if (modConfigInstance.isEnabled && modConfigInstance.showCps && (modConfigInstance.showLeftClickCPS || modConfigInstance.showRightClickCPS)) {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && !client.options.hudHidden) {

                if (modConfigInstance.showLeftClickCPS && client.mouse.wasLeftButtonClicked()) {
                    leftClickTimestamps.add(System.currentTimeMillis());
                }
                if (modConfigInstance.showRightClickCPS && client.mouse.wasRightButtonClicked()) {
                    rightClickTimestamps.add(System.currentTimeMillis());
                }

                updateCps();

                String text = "";
                if (modConfigInstance.showLeftClickCPS) {
                    text = String.valueOf(leftClickTimestamps.size());
                }
                if (modConfigInstance.showLeftClickCPS && modConfigInstance.showRightClickCPS) {
                    text += " | ";
                }
                if (modConfigInstance.showRightClickCPS) {
                    text += String.valueOf(rightClickTimestamps.size());
                }

                drawContext.drawText(client.textRenderer, text, modConfigInstance.cpsHudX, modConfigInstance.cpsHudY, modConfigInstance.cpsColor, modConfigInstance.cpsShadow);

            }

        }

    }

    public static void updateCps() {
        long currentTime = System.currentTimeMillis();
        if (!leftClickTimestamps.isEmpty() && currentTime - leftClickTimestamps.getFirst() > 1000) {
            leftClickTimestamps.pollFirst();
        }
        if (!rightClickTimestamps.isEmpty() && currentTime - rightClickTimestamps.getFirst() > 1000) {
            rightClickTimestamps.pollFirst();
        }
    }

}
