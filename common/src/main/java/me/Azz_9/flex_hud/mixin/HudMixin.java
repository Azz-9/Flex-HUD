package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.modules.customModules.Variables;

@Mixin(Hud.class)
public abstract class HudMixin {

	@Inject(method = "willPrioritizeExperienceInfo", at = @At("RETURN"), cancellable = true)
	private void willPrioritizeExperienceInfo(CallbackInfoReturnable<Boolean> cir) {
		if (Modules.getInstance().isEnabled.getValue() &&
				Modules.getInstance().compass.enabled.getValue() &&
				Modules.getInstance().compass.overrideLocatorBar.getValue()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "extractRenderState", at = @At("HEAD"))
	private void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		Variables.frame();
	}
}
