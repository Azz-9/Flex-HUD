package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.utils.ChromaColor;
import me.Azz_9.better_hud.client.utils.FaviconUtils;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.ServerAddress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ServerAddressOverlay extends HudElement {
	public boolean hideWhenOffline = true;

	public ServerAddressOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);
		byte[] favicon = null;

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden) {
			return;
		}

		String text = "";

		if (CLIENT.getCurrentServerEntry() != null) {

			text = CLIENT.getCurrentServerEntry().address;
			favicon = CLIENT.getCurrentServerEntry().getFavicon();

		} else if (!this.hideWhenOffline) {

			text = Text.translatable("better_hud.server_address.hud.offline").getString();

		} else if (Better_hudClient.isEditing) {

			text = "play.hypixel.net";

		}

		if (!text.isEmpty()) {

			MatrixStack matrices = drawContext.getMatrices();
			matrices.push();
			matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
			matrices.scale(this.scale, this.scale, 1.0f);

			//Identifier icon = FaviconUtils.registerServerIcon(favicon);

			//drawContext.drawTexture(RenderLayer::getGuiTextured, icon, 0, 0, 0, 0, 10, 10, 10, 10);
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
		return new ServerAddress(parent, 0);
	}
}
