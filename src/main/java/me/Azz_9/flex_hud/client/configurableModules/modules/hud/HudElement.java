package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import me.Azz_9.flex_hud.client.configurableModules.Configurable;
import me.Azz_9.flex_hud.client.utils.SpeedTester;

public interface HudElement extends Configurable {
	void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter);

	default void renderWithSpeedTest(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
		if (!isEnabled()) {
			return;
		}

		SpeedTester.start(getID());

		this.render(graphics, tickCounter);

		SpeedTester.end(getID());
	}

	boolean shouldNotRender();

	default Identifier getLayer() {
		return VanillaHudElements.CHAT;
	}
}
