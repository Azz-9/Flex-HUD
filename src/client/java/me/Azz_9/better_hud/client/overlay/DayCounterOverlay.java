package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.utils.ChromaColor;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.DayCounter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class DayCounterOverlay extends HudElement {

	public DayCounterOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
		this.enabled = false; // disable by default
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden || CLIENT.world == null) {
			return;
		}

		long time = CLIENT.world.getTimeOfDay() / 24000;
		Text text = Text.translatable("better_hud.day_counter.hud.prefix").append(" " + (int) time);

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.drawText(CLIENT.textRenderer, text, 0, 0, (chromaColor ? ChromaColor.getColor() : this.color), this.shadow);

		setWidth(text.getString());
		this.height = CLIENT.textRenderer.fontHeight;

		if (drawBackground) {
			drawContext.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor);
		}

		matrices.pop();
	}

	@Override
	public Screen getConfigScreen(Screen parent) {
		return new DayCounter(parent, 0);
	}
}
