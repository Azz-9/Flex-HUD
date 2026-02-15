package me.Azz_9.flex_hud.client.mixin.scoreboard;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Comparator;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {

	@Accessor("SCOREBOARD_ENTRY_COMPARATOR")
	Comparator<ScoreboardEntry> getScoreboardEntryComparator();
}
