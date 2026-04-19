package me.Azz_9.flex_hud.client.mixin.scoreboard;

import net.minecraft.client.gui.Hud;
import net.minecraft.world.scores.PlayerScoreEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Comparator;

@Mixin(Hud.class)
public interface HudAccessor {

	@Accessor("SCORE_DISPLAY_ORDER")
	Comparator<PlayerScoreEntry> getScoreDisplayOrder();
}
