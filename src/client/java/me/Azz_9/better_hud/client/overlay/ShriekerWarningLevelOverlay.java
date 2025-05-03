package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.utils.ChromaColor;
import me.Azz_9.better_hud.client.utils.ShriekerWarningLevelUtils;
import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.ShriekerWarningLevel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShriekerWarningLevelOverlay extends HudElement {
	private static final Logger log = LoggerFactory.getLogger(ShriekerWarningLevelOverlay.class);
	public boolean showWhenInDeepDark = true;

	public ShriekerWarningLevelOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden || CLIENT.player == null || CLIENT.world == null) {
			return;
		}

		PlayerEntity player = CLIENT.player;

		if (!this.showWhenInDeepDark || CLIENT.world.getBiome(player.getBlockPos()).getIdAsString().equals("minecraft:deep_dark") || Better_hudClient.isEditing) {

			String text = Text.translatable("better_hud.shrieker_warning_level.hud.prefix").getString() + ": " + ShriekerWarningLevelUtils.getLevel();

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
		return new ShriekerWarningLevel(parent, 0);
	}
}
