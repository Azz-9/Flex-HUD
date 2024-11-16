package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ComboCounterOverlay implements HudRenderCallback {

    private static int comboCounter = 0;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (!ModConfig.getInstance().isEnabled || !ModConfig.getInstance().showComboCounter || client == null) {
            return;
        }

        MatrixStack matrices = drawContext.getMatrices();

        matrices.push();
        matrices.translate(ModConfig.getInstance().comboCounterHudX, ModConfig.getInstance().comboCounterHudY, 0.0);

        drawContext.drawText(client.textRenderer, Text.of("combo: " + comboCounter), 0, 0, ModConfig.getInstance().comboCounterColor, ModConfig.getInstance().comboCounterShadow);

        matrices.pop();

    }

    public static void calculteCombo(PlayerEntity playerAttacking, Entity entityAttacked) {

        comboCounter += 1;

    }

    public static void resetCombo() {
        comboCounter = 0;
    }
}
//FIXME