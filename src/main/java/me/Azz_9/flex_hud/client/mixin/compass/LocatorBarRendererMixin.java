package me.Azz_9.flex_hud.client.mixin.compass;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.LocatorBar;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;

@Mixin(LocatorBar.class)
public abstract class LocatorBarRendererMixin {

	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
	private void extractBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ModulesHelper.getInstance().isEnabled.getValue() &&
				ModulesHelper.getInstance().compass.enabled.getValue() &&
				ModulesHelper.getInstance().compass.overrideLocatorBar.getValue()) {
			ci.cancel();
		}
	}

	@Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
	private void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ModulesHelper.getInstance().isEnabled.getValue() &&
				ModulesHelper.getInstance().compass.enabled.getValue() &&
				ModulesHelper.getInstance().compass.overrideLocatorBar.getValue()) {
			ci.cancel();
		}
	}
}
