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
import net.minecraft.util.math.Vec3d;

public class ReachDisplayOverlay implements HudRenderCallback {

    private static double reach = 0.0;
    private static long lastHitTime = -1;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (!ModConfig.getInstance().isEnabled || !ModConfig.getInstance().showReach || client == null) {
            return;
        }

        if (lastHitTime == -1 || System.currentTimeMillis() - lastHitTime > 5000) {
            reach = 0.0; // reset reach 5s after last hit
        }

        MatrixStack matrices = drawContext.getMatrices();

        matrices.push();
        matrices.translate(ModConfig.getInstance().reachHudX, ModConfig.getInstance().reachHudY, 0.0);

        String format = "%." + ModConfig.getInstance().reachDigits + "f";
        String formattedSpeed = String.format(format, reach);

        drawContext.drawText(client.textRenderer, Text.of(formattedSpeed + " blocks"), 0, 0, ModConfig.getInstance().reachColor, ModConfig.getInstance().reachShadow);

        matrices.pop();

    }

    public static void calculateReach(PlayerEntity playerAttacking, Entity entityAttacked) {

        Vec3d posAttacking = playerAttacking.getPos();
        Vec3d posAttacked = entityAttacked.getPos();

        reach = posAttacking.distanceTo(posAttacked);

        lastHitTime  = System.currentTimeMillis();

    }

}
