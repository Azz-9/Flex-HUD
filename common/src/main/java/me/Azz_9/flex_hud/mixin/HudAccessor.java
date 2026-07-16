package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.scores.Objective;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Hud.class)
public interface HudAccessor {

	@Invoker("extractBossOverlay")
	void invokeExtractBossOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

	@Invoker("displayScoreboardSidebar")
	void invokeDisplayScoreboardSidebar(GuiGraphicsExtractor graphics, Objective objective);
}
