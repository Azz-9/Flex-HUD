package me.Azz_9.flex_hud.client.modules.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import me.Azz_9.flex_hud.client.config.Configurable;
import me.Azz_9.flex_hud.client.debug.SpeedTester;
import me.Azz_9.flex_hud.platform.Services;

public interface HudElement extends Configurable {
	void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter);

	default void renderWithSpeedTest(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
		if (!shouldRunSpeedTest()) {
			return;
		}

		SpeedTester.start(getID());

		this.render(graphics, tickCounter);

		SpeedTester.end(getID());
	}

	default boolean shouldRunSpeedTest() {
		return isEnabled();
	}

	boolean shouldNotRender();

	default Identifier getLayer() {
		return Services.PLATFORM.getChatIdentifier();
	}
}
