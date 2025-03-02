package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.modMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class ReachOverlay extends HudElement {
    public int digits = 2;

    private static double reach = 0.0;
    private static long lastHitTime = -1;

    public ReachOverlay(double defaultX, double defaultY) {
        super(defaultX, defaultY);
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        super.onHudRender(drawContext, tickCounter);
        
        final MinecraftClient CLIENT = MinecraftClient.getInstance();

        if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null) {
            return;
        }

        if (lastHitTime == -1 || System.currentTimeMillis() - lastHitTime > 5000) {
            reach = 0.0; // reset reach 5s after last hit
        }

        String format = "%." + this.digits + "f";
        String formattedSpeed = String.format(format, reach);
        String text = formattedSpeed + " blocks";

        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        matrices.translate(this.x, this.y, 0);
        matrices.scale(this.scale, this.scale, 1.0f);

        drawContext.drawText(CLIENT.textRenderer, Text.of(text), 0, 0, this.color, this.shadow);

        matrices.pop();

        setWidth(text);
        this.height = CLIENT.textRenderer.fontHeight;

    }

    //TODO déplacer ça dans un util
    public static void calculateReach(PlayerEntity playerAttacking, Entity entityAttacked) {

        Vec3d posAttacking = playerAttacking.getPos();
        Vec3d posAttacked = entityAttacked.getPos();

        reach = posAttacking.distanceTo(posAttacked);

        lastHitTime  = System.currentTimeMillis();

    }
}
