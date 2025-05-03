package me.Azz_9.better_hud.client.overlay;

import me.Azz_9.better_hud.client.utils.ChromaColor;
import me.Azz_9.better_hud.modMenu.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MemoryUsageOverlay extends HudElement {

	public MemoryUsageOverlay(double defaultX, double defaultY) {
		super(defaultX, defaultY);
		this.enabled = false; // disable by default
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
		super.render(drawContext, tickCounter);

		final MinecraftClient CLIENT = MinecraftClient.getInstance();

		if (!ModConfig.getInstance().isEnabled || !this.enabled || CLIENT == null || CLIENT.options.hudHidden) {
			return;
		}

		String text = "Mem: " + getMemoryUsagePercentage() + "%";

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

	private int getMemoryUsagePercentage() {
		// Accéder au gestionnaire de mémoire de la JVM
		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

		// Obtenir les informations sur la mémoire heap
		MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

		// Mémoire utilisée et maximum allouée
		long usedMemory = heapMemoryUsage.getUsed();
		long maxMemory = heapMemoryUsage.getMax();

		// Calculer le pourcentage
		return (int) ((double) usedMemory / maxMemory * 100);
	}

	@Override
	public Screen getConfigScreen(Screen parent) {
		return new me.Azz_9.better_hud.screens.modsConfigScreen.mods.MemoryUsage(parent, 0);
	}
}