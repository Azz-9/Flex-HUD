package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.utils.CalculateSpeed;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.Speedometer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;

import static me.Azz_9.better_hud.screens.modsConfigScreen.mods.Speedometer.SpeedometerUnits.*;

public class SpeedometerOverlay extends HudElement {
    public int digits = 1;
    public Speedometer.SpeedometerUnits units = Speedometer.SpeedometerUnits.MPS;
    public boolean useKnotInBoat = false;

    public SpeedometerOverlay(double defaultX, double defaultY) {
        super(defaultX, defaultY);
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        final MinecraftClient CLIENT = MinecraftClient.getInstance();

        if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden || CLIENT.player == null || CLIENT.world == null) {
            return;
        }

        String formattedSpeed = getString(CLIENT.player);

        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        matrices.translate(this.x, this.y, 0);
        matrices.scale(this.scale, this.scale, 1.0f);

        drawContext.drawText(CLIENT.textRenderer, formattedSpeed, 0, 0, this.color, this.shadow);

        matrices.pop();

        setWidth(formattedSpeed);
        this.height = CLIENT.textRenderer.fontHeight;
    }

    private String getString(PlayerEntity player) {
        String format = "%." + this.digits + "f";
        String formattedSpeed = String.format(format, CalculateSpeed.getSpeed());

        if (this.units == KNOT || (this.useKnotInBoat && player.getVehicle() instanceof BoatEntity)) {
            formattedSpeed += " knots";
        } else if (this.units == KPH) {
            formattedSpeed += " km/h";
        } else if (this.units == MPH) {
            formattedSpeed += " mph";
        } else {
            formattedSpeed += " m/s";
        }
        return formattedSpeed;
    }
}