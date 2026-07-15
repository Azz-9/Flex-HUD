package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.LocatorBar;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.modules.Modules;

@Mixin(LocatorBar.class)
public abstract class LocatorBarMixin {

	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
	private void extractBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue() &&
				Modules.getInstance().compass.enabled.getValue() &&
				Modules.getInstance().compass.overrideLocatorBar.getValue()) {
			ci.cancel();
		}
	}

	@Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
	private void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue() &&
				Modules.getInstance().compass.enabled.getValue() &&
				Modules.getInstance().compass.overrideLocatorBar.getValue()) {
			ci.cancel();
		}
	}
}
