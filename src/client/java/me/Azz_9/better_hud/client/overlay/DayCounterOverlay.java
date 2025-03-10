package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.modMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class DayCounterOverlay extends HudElement {

	public DayCounterOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.onHudRender(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden || CLIENT.world == null) {
			return;
		}

		long time = CLIENT.world.getTimeOfDay() / 24000;
		Text text = Text.translatable("better_hud.day_counter.hud.prefix").append(" " + (int) time);

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(this.x, this.y, 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.drawText(CLIENT.textRenderer, text, 0, 0, this.color, this.shadow);

		matrices.pop();

		setWidth(text.getString());
		this.height = CLIENT.textRenderer.fontHeight;
	}

}
