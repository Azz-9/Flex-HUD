package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.scores.Objective;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface GuiAccessor {

	@Invoker("extractBossOverlay")
	void invokeExtractBossOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

	@Invoker("displayScoreboardSidebar")
	void invokeDisplayScoreboardSidebar(GuiGraphicsExtractor graphics, Objective objective);

	@Invoker("extractTitle")
	void invokeExtractTitle(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
}
