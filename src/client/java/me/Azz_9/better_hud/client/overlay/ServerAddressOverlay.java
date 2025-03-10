package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.modMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ServerAddressOverlay extends HudElement {
	public boolean hideWhenOffline = true;

	public ServerAddressOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.onHudRender(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden) {
			return;
		}

		String text = "";

		if (CLIENT.getCurrentServerEntry() != null) {

			text = CLIENT.getCurrentServerEntry().address;

		} else if (!this.hideWhenOffline) {

			text = Text.translatable("better_hud.ping.hud.offline").getString();

		} else if (Better_hudClient.isEditing) {

			text = "play.hypixel.net";

		}

		if (!text.isEmpty()) {

			MatrixStack matrices = drawContext.getMatrices();
			matrices.push();
			matrices.translate(this.x, this.y, 0);
			matrices.scale(this.scale, this.scale, 1.0f);

			drawContext.drawText(CLIENT.textRenderer, text, 0, 0, this.color, this.shadow);

			matrices.pop();

			setWidth(text);
			this.height = CLIENT.textRenderer.fontHeight;
		}

	}

}
