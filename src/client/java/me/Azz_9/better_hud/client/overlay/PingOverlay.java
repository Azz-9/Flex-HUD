package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.utils.ChromaColor;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.Ping;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PingOverlay extends HudElement {
	public boolean hideWhenOffline = true;

	public PingOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden || CLIENT.player == null) {
			return;
		}

		String text = "";

		if (CLIENT.getCurrentServerEntry() != null) {

			if (CLIENT.getNetworkHandler() != null) {
				PlayerListEntry entry = CLIENT.getNetworkHandler().getPlayerListEntry(CLIENT.player.getUuid());

				if (entry != null) {
					int latency = entry.getLatency();

					text = latency + " ms";
				}
			}

		} else if (!this.hideWhenOffline) {

			text = Text.translatable("better_hud.ping.hud.offline").getString();

		} else if (Better_hudClient.isEditing) {

			text = "20 ms";

		}

		if (!text.isEmpty()) {

			MatrixStack matrices = drawContext.getMatrices();
			matrices.push();
			matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
			matrices.scale(this.scale, this.scale, 1.0f);

			drawContext.drawText(CLIENT.textRenderer, text, 0, 0, (chromaColor ? ChromaColor.getColor() : this.color), this.shadow);

			setWidth(text);
			this.height = CLIENT.textRenderer.fontHeight;

			if (drawBackground) {
				drawContext.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, 0x7f000000 | backgroundColor);
			}

			matrices.pop();
		}
	}

	@Override
	public Screen getConfigScreen(Screen parent) {
		return new Ping(parent, 0);
	}
}