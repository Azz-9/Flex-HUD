package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.client.utils.CalculateCps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class CPSOverlay extends HudElement {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);

        ModConfig INSTANCE = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!INSTANCE.isEnabled || !INSTANCE.showCps || !(INSTANCE.showLeftClickCPS || INSTANCE.showRightClickCPS) || client == null || client.options.hudHidden) {
            return;
        }

        this.x = INSTANCE.cpsHudX;
        this.y = INSTANCE.cpsHudY;

        String text = "";
        if (INSTANCE.showLeftClickCPS) {
            text = String.valueOf(CalculateCps.getInstance().getLeftCps());
        }
        if (INSTANCE.showLeftClickCPS && INSTANCE.showRightClickCPS) {
            text += " | ";
        }
        if (INSTANCE.showRightClickCPS) {
            text += String.valueOf(CalculateCps.getInstance().getRightCps());
        }

        drawContext.drawText(client.textRenderer, text, INSTANCE.cpsHudX, INSTANCE.cpsHudY, INSTANCE.cpsColor, INSTANCE.cpsShadow);

        setWidth(text);
        this.height = client.textRenderer.fontHeight;
    }

    @Override
    public void setPos(int x, int y) {
        ModConfig INSTANCE = ModConfig.getInstance();
        INSTANCE.cpsHudX = x;
        INSTANCE.cpsHudY = y;
    }

    @Override
    public boolean isEnabled() {
        ModConfig INSTANCE = ModConfig.getInstance();
        return INSTANCE.showCps && (INSTANCE.showLeftClickCPS || INSTANCE.showRightClickCPS);
    }
}
