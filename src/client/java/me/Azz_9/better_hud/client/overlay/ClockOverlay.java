package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.utils.ChromaColor;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.Clock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockOverlay extends HudElement {
	public String textFormat = "hh:mm:ss";
	public boolean isTwentyFourHourFormat = true;

	public ClockOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden) {
			return;
		}

		String currentTime = getCurrentTime();

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.drawText(CLIENT.textRenderer, currentTime, 0, 0, (chromaColor ? ChromaColor.getColor() : this.color), this.shadow);

		setWidth(currentTime);
		this.height = CLIENT.textRenderer.fontHeight;

		if (drawBackground) {
			drawContext.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor);
		}

		matrices.pop();
	}

	public String getCurrentTime() {
		String textFormat = this.textFormat.toLowerCase();
		if (this.isTwentyFourHourFormat) {
			textFormat = textFormat.replace("hh", "HH");
		} else {
			textFormat += " a";
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(textFormat);
		return LocalTime.now().format(formatter);
	}

	@Override
	public Screen getConfigScreen(Screen parent) {
		return new Clock(parent, 0);
	}
}
