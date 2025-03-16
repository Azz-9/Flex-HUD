package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.utils.CalculateReach;
import me.Azz_9.better_hud.modMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ReachOverlay extends HudElement {
	public int digits = 2;

	public ReachOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
		this.enabled = false; // disable by default
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.onHudRender(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null) {
			return;
		}

		if (CalculateReach.getLastHitTime() == -1 || System.currentTimeMillis() - CalculateReach.getLastHitTime() > 5000) {
			CalculateReach.resetReach(); // reset reach 5s after last hit
		}

		String format = "%." + this.digits + "f";
		String formattedSpeed = String.format(format, CalculateReach.getReach());
		Text text = Text.literal(formattedSpeed).append(" ").append(Text.translatable("better_hud.reach.hud.unit"));

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.drawText(CLIENT.textRenderer, Text.of(text), 0, 0, this.color, this.shadow);

		matrices.pop();

		setWidth(text.getString());
		this.height = CLIENT.textRenderer.fontHeight;

	}
}
