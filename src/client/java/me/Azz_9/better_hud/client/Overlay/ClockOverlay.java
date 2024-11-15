package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        ModConfig modConfigInstance = ModConfig.getInstance();

        if (modConfigInstance.isEnabled && modConfigInstance.showClock) {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && !client.options.hudHidden) {

                drawContext.drawText(client.textRenderer, getCurrentTime(), modConfigInstance.clockHudX, modConfigInstance.clockHudY, modConfigInstance.clockColor, modConfigInstance.clockShadow);

            }

        }

    }

    public static String getCurrentTime() {
        String textFormat = ModConfig.getInstance().clockTextFormat.toLowerCase();
        if (ModConfig.getInstance().clock24hourformat) {
            textFormat = textFormat.replace("hh", "HH");
        } else {
            textFormat += " a";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(textFormat);
        return LocalTime.now().format(formatter);
    }

}
