package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public interface BossHealthOverlayAccessor {

	@Accessor("events")
	Map<UUID, LerpingBossEvent> getBossBars();

	@Accessor("BAR_WIDTH")
	static int getBarWidth() {
		throw new AssertionError();
	}

	@Accessor("BAR_HEIGHT")
	static int getBarHeight() {
		throw new AssertionError();
	}
}
