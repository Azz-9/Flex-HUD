package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class DayCounterOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        ModConfig modConfigInstance = ModConfig.getInstance();

        if (modConfigInstance.isEnabled && modConfigInstance.showDayCounter) {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && !client.options.hudHidden && client.world != null) {

                long time = client.world.getTimeOfDay() / 24000;

                drawContext.drawText(client.textRenderer,"Day " + (int) time, modConfigInstance.dayCounterHudX, modConfigInstance.dayCounterHudY, modConfigInstance.dayCounterColor, modConfigInstance.dayCounterShadow);


            }

        }

    }

}
