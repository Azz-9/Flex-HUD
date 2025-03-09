package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.modMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ComboCounterOverlay extends HudElement {

	private static int comboCounter = 0;

	public ComboCounterOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.onHudRender(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden) {
			return;
		}

		MatrixStack matrices = drawContext.getMatrices();
		matrices.push();
		matrices.translate(this.x, this.y, 0);
		matrices.scale(this.scale, this.scale, 1.0f);

		drawContext.drawText(CLIENT.textRenderer, Text.of("combo: " + comboCounter), 0, 0, this.color, this.shadow);

		matrices.pop();

	}

	public static void calculteCombo(PlayerEntity playerAttacking, Entity entityAttacked) {

		comboCounter += 1;

	}

	public static void resetCombo() {
		comboCounter = 0;
	}
}
//FIXME