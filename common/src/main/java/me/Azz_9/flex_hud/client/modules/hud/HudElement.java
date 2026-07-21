package me.Azz_9.flex_hud.client.modules.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import me.Azz_9.flex_hud.client.config.Configurable;
import me.Azz_9.flex_hud.client.debug.PerfTester;
import me.Azz_9.flex_hud.platform.Services;

public interface HudElement extends Configurable {
	void render(GuiGraphics graphics, DeltaTracker tickCounter);

	default void renderWithPerfTest(GuiGraphics graphics, DeltaTracker tickCounter) {
		if (!shouldRunSpeedTest()) {
			this.render(graphics, tickCounter);
			return;
		}

		PerfTester.testFrame(getID(), () -> render(graphics, tickCounter));
	}

	default boolean shouldRunSpeedTest() {
		return isEnabled();
	}

	boolean shouldNotRender();

	default Identifier getLayer() {
		return Services.PLATFORM.getChatIdentifier();
	}
}
