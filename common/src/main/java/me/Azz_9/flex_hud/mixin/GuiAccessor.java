package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.scores.Objective;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface GuiAccessor {

	@Invoker("renderBossOverlay")
	void invokeRenderBossOverlay(GuiGraphics graphics, DeltaTracker deltaTracker);

	@Invoker("displayScoreboardSidebar")
	void invokeDisplayScoreboardSidebar(GuiGraphics graphics, Objective objective);

	@Invoker("renderTitle")
	void invokeRenderTitle(GuiGraphics graphics, DeltaTracker deltaTracker);
}
