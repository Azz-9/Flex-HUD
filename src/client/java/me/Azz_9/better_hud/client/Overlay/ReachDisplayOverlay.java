package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class ReachDisplayOverlay extends HudElement {

    private static double reach = 0.0;
    private static long lastHitTime = -1;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);

        ModConfig INSTANCE = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!INSTANCE.isEnabled || !INSTANCE.showReach || client == null) {
            return;
        }

        this.x = INSTANCE.reachHudX;
        this.y = INSTANCE.reachHudY;

        if (lastHitTime == -1 || System.currentTimeMillis() - lastHitTime > 5000) {
            reach = 0.0; // reset reach 5s after last hit
        }

        MatrixStack matrices = drawContext.getMatrices();

        matrices.push();
        matrices.translate(INSTANCE.reachHudX, INSTANCE.reachHudY, 0.0);

        String format = "%." + INSTANCE.reachDigits + "f";
        String formattedSpeed = String.format(format, reach);
        String text = formattedSpeed + " blocks";

        drawContext.drawText(client.textRenderer, Text.of(text), 0, 0, INSTANCE.reachColor, INSTANCE.reachShadow);

        matrices.pop();

        setWidth(text);
        this.height = client.textRenderer.fontHeight;

    }

    public static void calculateReach(PlayerEntity playerAttacking, Entity entityAttacked) {

        Vec3d posAttacking = playerAttacking.getPos();
        Vec3d posAttacked = entityAttacked.getPos();

        reach = posAttacking.distanceTo(posAttacked);

        lastHitTime  = System.currentTimeMillis();

    }

    @Override
    public void setPos(int x, int y) {
        ModConfig INSTANCE = ModConfig.getInstance();
        INSTANCE.reachHudX = x;
        INSTANCE.reachHudY = y;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.getInstance().showReach;
    }
}
