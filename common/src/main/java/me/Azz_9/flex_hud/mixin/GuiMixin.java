package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.modules.Modules;

@Mixin(Hud.class)
public abstract class GuiMixin {

	@Inject(method = "extractScoreboardSidebar", at = @At("HEAD"), cancellable = true)
	private void extractScoreboardSidebar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue() && Modules.getInstance().scoreboard.enabled.getValue()) {
			ci.cancel();
		}
	}
}
