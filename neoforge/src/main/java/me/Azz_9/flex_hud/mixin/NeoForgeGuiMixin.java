package me.Azz_9.flex_hud.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import me.Azz_9.flex_hud.client.modules.Modules;

@Mixin(Gui.class)
public abstract class NeoForgeGuiMixin {

	@ModifyExpressionValue(
			method = "lambda$displayScoreboardSidebar$17",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/scores/PlayerScoreEntry;formatValue(Lnet/minecraft/network/chat/numbers/NumberFormat;)Lnet/minecraft/network/chat/MutableComponent;"
			)
	)
	private MutableComponent modifyScoreString(MutableComponent original) {
		if (Modules.getInstance().isEnabled.getValue()
				&& Modules.getInstance().scoreboard.enabled.getValue()
				&& !Modules.getInstance().scoreboard.showScore.getValue()) {
			return Component.empty();
		}
		return original;
	}

	@ModifyExpressionValue(
			method = "lambda$displayScoreboardSidebar$17",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/Font;width(Lnet/minecraft/network/chat/FormattedText;)I"
			)
	)
	private int modifyScoreWidth(int original) {
		if (Modules.getInstance().isEnabled.getValue()
				&& Modules.getInstance().scoreboard.enabled.getValue()
				&& !Modules.getInstance().scoreboard.showScore.getValue()) {
			return 0;
		}
		return original;
	}
}
