package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class DirectionOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        ModConfig modConfigInstance = ModConfig.getInstance();

        if (modConfigInstance.isEnabled && modConfigInstance.showDirection) {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && !client.options.hudHidden) {

                PlayerEntity p = client.player;

                if (p != null) {
                    int screenWidth = client.getWindow().getScaledWidth();
                    float centerX = screenWidth / 2.0f;

                    // Calcul de la direction (yaw)
                    float yaw = (p.getYaw() % 360 + 360) % 360;

                    MatrixStack matrices = drawContext.getMatrices();

                    // Affichage des points cardinaux
                    drawContext.enableScissor((int) centerX - 106, 0, (int) centerX + 106, 30);

                    drawCompassPoint(drawContext, matrices, "S", 0, yaw);
                    drawCompassPoint(drawContext, matrices, "SW", 45, yaw);
                    drawCompassPoint(drawContext, matrices, "W", 90, yaw);
                    drawCompassPoint(drawContext, matrices, "NW", 135, yaw);
                    drawCompassPoint(drawContext, matrices, "N", 180, yaw);
                    drawCompassPoint(drawContext, matrices, "NE", 225, yaw);
                    drawCompassPoint(drawContext, matrices, "E", 270, yaw);
                    drawCompassPoint(drawContext, matrices, "SE", 315, yaw);

                    if (modConfigInstance.showIntermediateDirectionPoint) {
                        for (int i = 0; i < 8; i++) {
                            drawIntermediatePoint(drawContext, matrices, 15 * i * 3 + 15, yaw);
                            drawIntermediatePoint(drawContext, matrices, 15 * i * 3 + 30, yaw);
                        }
                    }

                    drawContext.disableScissor();

                    if (modConfigInstance.showDirectionMarker) {
                        matrices.push();
                        matrices.translate(centerX - (client.textRenderer.getWidth("▼") / 2.0f), 0.0f, 0.0f);
                        matrices.scale(1.0f, 0.5f, 1.0f);
                        drawContext.drawText(client.textRenderer, "▼", 0, 0, modConfigInstance.directionColor, modConfigInstance.directionShadow);
                        matrices.pop();
                    }

                }

            }

        }

    }

    private void drawCompassPoint(DrawContext drawContext, MatrixStack matrices, String label, int angle, float yaw) {
        ModConfig modConfigInstance = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();

        float angleDifference = (angle - yaw + 540) % 360 - 180;

        if (Math.abs(angleDifference) <= 100) {
            // Calculer la position X de chaque point cardinal en fonction de l'angle
            float positionX = (screenWidth / 2.0f) + (angleDifference * (screenWidth / 720.0f));
            positionX = positionX - (client.textRenderer.getWidth(label) / 1.6f);

            // Afficher le label des directions avec couleur et taille de texte ajustée
            matrices.push();
            matrices.translate(positionX, 10, 0);
            matrices.scale(1.25f, 1.25f, 1.5f); // make the text 1.5 times bigger
            drawContext.drawText(client.textRenderer, label, 0, 0, modConfigInstance.directionColor, modConfigInstance.directionShadow);
            matrices.pop();

        }
    }

    private void drawIntermediatePoint(DrawContext drawContext, MatrixStack matrices, int angle, float yaw) {
        ModConfig modConfigInstance = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();

        float angleDifference = (angle - yaw + 540) % 360 - 180;

        if (Math.abs(angleDifference) <= 100) {
            // Calculer la position X de chaque point cardinal en fonction de l'angle
            float positionX = (screenWidth / 2.0f) + (angleDifference * (screenWidth / 720.0f));

            matrices.push();
            matrices.translate(positionX - (client.textRenderer.getWidth("|") / 2.0f), 12, 0);
            matrices.scale(1.0f, 0.75f, 1.0f);
            drawContext.drawText(client.textRenderer, "|", 0, 0, modConfigInstance.directionColor, modConfigInstance.directionShadow);
            matrices.pop();


            matrices.push();
            matrices.translate(positionX - (client.textRenderer.getWidth(String.valueOf(angle)) / 4.0f), 20, 0);
            matrices.scale(0.5f, 0.5f, 1.0f); // 2 times smaller
            drawContext.drawText(client.textRenderer, String.valueOf(angle), 0, 0, modConfigInstance.directionColor, modConfigInstance.directionShadow);
            matrices.pop();

        }

    }

}
// TODO faire l'effet fondu sur les extrémités de la boussole (trop dur aled)
// TODO ajouter la posibilité de changer l'endroit ou la boussole s'affiche sur l'écran