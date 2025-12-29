package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import me.Azz_9.flex_hud.client.configurableModules.Configurable;
import me.Azz_9.flex_hud.client.utils.SpeedTester;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public interface HudElement extends Configurable {
	void render(GuiGraphics context, DeltaTracker tickCounter);

	default void renderWithSpeedTest(GuiGraphics context, DeltaTracker tickCounter) {
		if (!isEnabled()) {
			return;
		}

		SpeedTester.start(getID());

		this.render(context, tickCounter);

		SpeedTester.end(getID());
	}

	boolean shouldNotRender();

	default Identifier getLayer() {
		return VanillaHudElements.CHAT;
	}
}
