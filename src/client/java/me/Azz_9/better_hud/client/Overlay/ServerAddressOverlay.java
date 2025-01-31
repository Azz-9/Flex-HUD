package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.client.Better_hudClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class ServerAddressOverlay extends HudElement {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);

        ModConfig INSTANCE = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!INSTANCE.isEnabled || !INSTANCE.showServerAddress || client == null || client.options.hudHidden) {
            return;
        }

        this.x = INSTANCE.serverAddressHudX;
        this.y = INSTANCE.serverAddressHudY;

        String text = "";

        if (client.getCurrentServerEntry() != null) {

            text = client.getCurrentServerEntry().address;

            drawContext.drawText(client.textRenderer, text, INSTANCE.serverAddressHudX, INSTANCE.serverAddressHudY, INSTANCE.serverAddressColor, INSTANCE.serverAddressShadow);

        } else if (!INSTANCE.hideServerAddressWhenOffline) {

            text = "Offline";
            drawContext.drawText(client.textRenderer, text, INSTANCE.serverAddressHudX, INSTANCE.serverAddressHudY, INSTANCE.serverAddressColor, INSTANCE.serverAddressShadow);

        } else if (Better_hudClient.isEditing) {

            text = "play.hypixel.net";
            drawContext.drawText(client.textRenderer, text, INSTANCE.serverAddressHudX, INSTANCE.serverAddressHudY, INSTANCE.serverAddressColor, INSTANCE.serverAddressShadow);
        }

        if (!text.isEmpty()) {
            setWidth(text);
            this.height = client.textRenderer.fontHeight;
        }

    }

    @Override
    public void setPos(int x, int y) {
        ModConfig INSTANCE = ModConfig.getInstance();
        INSTANCE.serverAddressHudX = x;
        INSTANCE.serverAddressHudY = y;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.getInstance().showServerAddress;
    }

}
