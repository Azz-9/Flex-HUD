package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockOverlay extends HudElement {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);

        ModConfig INSTANCE = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!INSTANCE.isEnabled || !INSTANCE.showClock || client == null ||client.options.hudHidden) {
            return;
        }

        this.x = INSTANCE.clockHudX;
        this.y = INSTANCE.clockHudY;

        String currentTime = getCurrentTime();
        drawContext.drawText(client.textRenderer, getCurrentTime(), INSTANCE.clockHudX, INSTANCE.clockHudY, INSTANCE.clockColor, INSTANCE.clockShadow);

        setWidth(currentTime);
        this.height = client.textRenderer.fontHeight;
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

    @Override
    public void setPos(int x, int y) {
        ModConfig INSTANCE = ModConfig.getInstance();
        INSTANCE.clockHudX = x;
        INSTANCE.clockHudY = y;
    }

    @Override
    public boolean isEnabled() {
        ModConfig INSTANCE = ModConfig.getInstance();
        return ModConfig.getInstance().showClock && !INSTANCE.clockTextFormat.isEmpty();
    }
}
