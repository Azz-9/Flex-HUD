package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

public class FPSOverlay extends HudElement {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);

        ModConfig INSTANCE = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!INSTANCE.isEnabled || !INSTANCE.showFPS || client == null || client.options.hudHidden) {
            return;
        }

        this.x = INSTANCE.FPSHudX;
        this.y = INSTANCE.FPSHudY;

        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        matrices.translate(INSTANCE.FPSHudX, INSTANCE.FPSHudY, 0);
        matrices.scale(INSTANCE.FPSscale, INSTANCE.FPSscale, 1.0f);

        String text = client.getCurrentFps() + " FPS";
        drawContext.drawText(client.textRenderer, text, 0, 0, INSTANCE.FPSColor, INSTANCE.FPSShadow);

        matrices.pop();

        setWidth(text);
        this.height = client.textRenderer.fontHeight;
    }

    @Override
    public void setScale(float scale) {
        ModConfig.getInstance().FPSscale = scale;
    }

    @Override
    public void setPos(int x, int y) {
        ModConfig INSTANCE = ModConfig.getInstance();
        INSTANCE.FPSHudX = x;
        INSTANCE.FPSHudY = y;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.getInstance().showFPS;
    }
}
