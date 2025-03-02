package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.modMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class BossBarOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (!ModConfig.getInstance().isEnabled || client == null || client.options.hudHidden) {
            return;
        }

        //TODO à faire


    }

}
