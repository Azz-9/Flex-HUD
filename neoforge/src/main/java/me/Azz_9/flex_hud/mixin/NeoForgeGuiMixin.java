package me.Azz_9.flex_hud.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.Objective;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import me.Azz_9.flex_hud.utils.ScoreboardMixinHelper;

@Mixin(Gui.class)
public abstract class NeoForgeGuiMixin {

	@ModifyVariable(
			method = "displayScoreboardSidebar",
			at = @At("STORE"),
			index = 18
	)
	private int beforeRender(
			int headerY,
			GuiGraphics graphics,
			Objective objective,
			@Local(index = 8) int biggestWidth,
			@Local(index = 11) int height,
			@Local(index = 12) LocalIntRef bottom,
			@Local(index = 14) LocalIntRef left,
			@Local(index = 15) LocalIntRef right,
			@Local(index = 16) LocalIntRef backgroundColor,
			@Local(index = 17) LocalIntRef headerBackgroundColor
	) {
		return ScoreboardMixinHelper.beforeRender(
				headerY, graphics, biggestWidth, height, bottom, left, right,
				backgroundColor, headerBackgroundColor
		);
	}

	@ModifyExpressionValue(
			method = "lambda$displayScoreboardSidebar$13",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/scores/PlayerScoreEntry;formatValue(Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"
			)
	)
	private MutableComponent modifyScoreString(MutableComponent original) {
		return ScoreboardMixinHelper.modifyScoreString(original);
	}

	@ModifyExpressionValue(
			method = "lambda$displayScoreboardSidebar$13",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/Font;width(Lnet/minecraft/network/chat/FormattedText;)I"
			)
	)
	private int modifyScoreWidth(int original) {
		return ScoreboardMixinHelper.modifyScoreWidth(original);
	}
}
