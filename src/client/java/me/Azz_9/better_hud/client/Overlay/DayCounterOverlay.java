package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class DayCounterOverlay extends HudElement {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);

        ModConfig INSTANCE = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!INSTANCE.isEnabled || !INSTANCE.showDayCounter || client == null || client.options.hudHidden || client.world == null) {
            return;
        }

        this.x = INSTANCE.dayCounterHudX;
        this.y = INSTANCE.dayCounterHudY;

        long time = client.world.getTimeOfDay() / 24000;
        String text = "Day " + (int) time;

        drawContext.drawText(client.textRenderer,text, INSTANCE.dayCounterHudX, INSTANCE.dayCounterHudY, INSTANCE.dayCounterColor, INSTANCE.dayCounterShadow);

        updateWidth(text);
        this.height = client.textRenderer.fontHeight;
    }

    @Override
    public void setPos(int x, int y) {
        ModConfig INSTANCE = ModConfig.getInstance();
        INSTANCE.dayCounterHudX = x;
        INSTANCE.dayCounterHudY = y;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.getInstance().showDayCounter;
    }

}
