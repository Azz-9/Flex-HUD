package me.Azz_9.flex_hud.client.mixin.crosshair;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;

@Mixin(Hud.class)
public abstract class CrosshairMixin {

	@Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true)
	private void extractCrosshair(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ModulesHelper.getInstance().isEnabled.getValue() &&
				ModulesHelper.getInstance().crosshair.enabled.getValue()) {
			ci.cancel();
		}
	}
}
