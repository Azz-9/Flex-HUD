package me.Azz_9.better_hud.client.Overlay;

import me.Azz_9.better_hud.ModMenu.ModConfig;
import me.Azz_9.better_hud.client.utils.CalculateSpeed;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.jetbrains.annotations.NotNull;

import static me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods.Speedometer.SpeedometerUnits.*;

public class SpeedometerOverlay extends HudElement {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        ModConfig INSTANCE = ModConfig.getInstance();
        MinecraftClient client = MinecraftClient.getInstance();

        if (!INSTANCE.isEnabled || !INSTANCE.showSpeedometer || client == null || client.options.hudHidden || client.player == null || client.world == null) {
            return;
        }
        
        this.x = INSTANCE.speedometerHudX;
        this.y = INSTANCE.speedometerHudY;

        PlayerEntity player = client.player;

        MatrixStack matrices = drawContext.getMatrices();

        matrices.push();
        matrices.translate(this.x, this.y, 0);

        String formattedSpeed = getString(player, INSTANCE);

        drawContext.drawText(client.textRenderer, formattedSpeed, 0, 0, INSTANCE.speedometerColor, INSTANCE.speedometerShadow);

        matrices.pop();

        setWidth(formattedSpeed);
        this.height = client.textRenderer.fontHeight;
    }

    private static @NotNull String getString(PlayerEntity player, ModConfig INSTANCE) {
        String format = "%." + INSTANCE.speedometerDigits + "f";
        String formattedSpeed = String.format(format, CalculateSpeed.getSpeed());

        if (INSTANCE.speedometerUnits == KNOT || (INSTANCE.useKnotInBoat && player.getVehicle() instanceof BoatEntity)) {
            formattedSpeed += " knots";
        } else if (INSTANCE.speedometerUnits == KPH) {
            formattedSpeed += " km/h";
        } else if (INSTANCE.speedometerUnits == MPH) {
            formattedSpeed += " mph";
        } else {
            formattedSpeed += " m/s";
        }
        return formattedSpeed;
    }

    @Override
    public void setPos(int x, int y) {
        ModConfig INSTANCE = ModConfig.getInstance();
        INSTANCE.speedometerHudX = x;
        INSTANCE.speedometerHudY = y;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.getInstance().showSpeedometer;
    }
}