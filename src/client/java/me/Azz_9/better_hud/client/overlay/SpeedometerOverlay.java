package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.utils.CalculateSpeed;
import me.Azz_9.better_hud.client.utils.ChromaColor;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.Speedometer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.text.Text;

import static me.Azz_9.better_hud.screens.modsConfigScreen.mods.Speedometer.SpeedometerUnits.*;

public class SpeedometerOverlay extends HudElement {
	public int digits = 1;
	public Speedometer.SpeedometerUnits units = Speedometer.SpeedometerUnits.MPS;
	public boolean useKnotInBoat = false;

	public SpeedometerOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
		this.enabled = false; // disable by default
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden || CLIENT.player == null || CLIENT.world == null) {
			return;
		}

		String formattedSpeed = getString(CLIENT.player);

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.drawText(CLIENT.textRenderer, formattedSpeed, 0, 0, (chromaColor ? ChromaColor.getColor() : this.color), this.shadow);

		setWidth(formattedSpeed);
		this.height = CLIENT.textRenderer.fontHeight;

		if (drawBackground) {
			drawContext.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor);
		}

		matrices.pop();
	}

	private String getString(PlayerEntity player) {
		String format = "%." + this.digits + "f";
		String formattedSpeed = String.format(format, CalculateSpeed.getSpeed());

		if (this.units == KNOT || (this.useKnotInBoat && player.getVehicle() instanceof BoatEntity)) {
			formattedSpeed += " " + Text.translatable("better_hud.speedometer.hud.units.knots").getString();
		} else if (this.units == KPH) {
			formattedSpeed += " " + Text.translatable("better_hud.speedometer.hud.units.kph").getString();
		} else if (this.units == MPH) {
			formattedSpeed += " " + Text.translatable("better_hud.speedometer.hud.units.mph").getString();
		} else {
			formattedSpeed += " " + Text.translatable("better_hud.speedometer.hud.units.mps").getString();
		}
		return formattedSpeed;
	}

	@Override
	public Screen getConfigScreen(Screen parent) {
		return new Speedometer(parent, 0);
	}
}