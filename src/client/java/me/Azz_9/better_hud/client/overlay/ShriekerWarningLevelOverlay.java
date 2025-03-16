package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.modMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ShriekerWarningLevelOverlay extends HudElement {
	public boolean showWhenInDeepDark = true;

	public ShriekerWarningLevelOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.onHudRender(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden || CLIENT.player == null || CLIENT.world == null) {
			return;
		}

		PlayerEntity player = CLIENT.player;

		int warningLevel = 0;
		if (!this.showWhenInDeepDark || CLIENT.world.getBiome(player.getBlockPos()).getIdAsString().equals("minecraft:deep_dark")) {

			if (player.getSculkShriekerWarningManager().isPresent()) {
				warningLevel = player.getSculkShriekerWarningManager().get().getWarningLevel();
			}

			MatrixStack matrices = drawContext.getMatrices();
			matrices.push();
			matrices.translate(Math.round(this.x * vw), Math.round(this.y * vh), 0);
			matrices.scale(this.scale, this.scale, 1.0f);

			drawContext.drawText(CLIENT.textRenderer, Text.translatable("better_hud.shrieker_warning_level.hud.prefix").getString() + ": " + warningLevel, (int) this.x, (int) this.y, this.color, this.shadow);

			matrices.pop();
		}
	}
}
