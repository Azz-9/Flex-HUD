package me.Azz_9.flex_hud.client.mixin.scoreboard;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.scores.PlayerScoreEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Comparator;

@Mixin(Gui.class)
public interface GuiAccessor {

	@Accessor("SCORE_DISPLAY_ORDER")
	Comparator<PlayerScoreEntry> getScoreDisplayOrder();
}
