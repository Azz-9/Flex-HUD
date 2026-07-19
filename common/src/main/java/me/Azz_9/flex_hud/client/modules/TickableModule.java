package me.Azz_9.flex_hud.client.modules;

import me.Azz_9.flex_hud.client.config.Activable;
import me.Azz_9.flex_hud.client.debug.PerfTester;

public interface TickableModule extends Activable {

	default boolean shouldTick() {
		return isEnabled();
	}

	void tick();

	default void tickWithPerfTest() {
		PerfTester.startTick(getClass().getSimpleName());

		tick();

		PerfTester.endTick(getClass().getSimpleName());
	}
}
