package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import static me.Azz_9.better_hud.ModMenu.Enum.SpeedometerUnitsEnum.*;

public class SpeedometerOverlay implements HudRenderCallback {

    private static Vec3d previousPosition = null;
    private static double speed = 0.0;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        ModConfig modConfigInstance = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!modConfigInstance.isEnabled || !modConfigInstance.showSpeedometer || client == null || client.options.hudHidden || client.player == null || client.world == null) {
            return;
        }

        PlayerEntity player = client.player;

        MatrixStack matrices = drawContext.getMatrices();

        int x = modConfigInstance.speedometerHudX;
        int y = modConfigInstance.speedometerHudY;

        matrices.push();
        matrices.translate(x, y, 0);

        String formattedSpeed = getString(player);

        drawContext.drawText(client.textRenderer, formattedSpeed, 0, 0, ModConfig.getInstance().speedometerColor, ModConfig.getInstance().speedometerShadow);

        matrices.pop();
    }

    private static @NotNull String getString(PlayerEntity player) {
        String format = "%." + ModConfig.getInstance().speedometerDigits + "f";
        String formattedSpeed = String.format(format, speed);

        if (ModConfig.getInstance().speedometerUnits == KNOT || (ModConfig.getInstance().useKnotInBoat && player.getVehicle() instanceof BoatEntity)) {
            formattedSpeed += " knots";
        } else if (ModConfig.getInstance().speedometerUnits == KPH) {
            formattedSpeed += " km/h";
        } else if (ModConfig.getInstance().speedometerUnits == MPH) {
            formattedSpeed += " mph";
        } else {
            formattedSpeed += " m/s";
        }
        return formattedSpeed;
    }

    public static void calculateSpeed(PlayerEntity player) {
        Vec3d currentPosition = player.getPos();
        if (previousPosition != null) {
            Vec3d currentVector = new Vec3d(
                    currentPosition.x - previousPosition.x,
                    currentPosition.y - previousPosition.y,
                    currentPosition.z - previousPosition.z
            );
            speed = Math.sqrt(Math.pow(currentVector.x, 2) + Math.pow(currentVector.y, 2) + Math.pow(currentVector.z, 2)) * 20; // speed in blocks per seconds

            if (ModConfig.getInstance().speedometerUnits == KNOT || (ModConfig.getInstance().useKnotInBoat && player.getVehicle() instanceof BoatEntity)) {
                speed = speed * 1.9438452492;
            } else if (ModConfig.getInstance().speedometerUnits == KPH) {
                speed = speed * 3.6;
            } else if (ModConfig.getInstance().speedometerUnits == MPH) {
                speed = speed * 2.2369362921;
            } // no need MPS because speed is already in meters per seconds

        }
        previousPosition = currentPosition;
    }

}
