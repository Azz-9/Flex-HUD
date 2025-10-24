package me.Azz_9.flex_hud.client.mixin.compass;

import me.Azz_9.flex_hud.client.configurableModules.JsonConfigHelper;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	@Inject(method = "shouldShowExperienceBar", at = @At("RETURN"), cancellable = true)
	private void shouldShowExperienceBar(CallbackInfoReturnable<Boolean> cir) {
		if (JsonConfigHelper.getInstance().isEnabled && JsonConfigHelper.getInstance().compass.enabled.getValue() && JsonConfigHelper.getInstance().compass.overrideLocatorBar.getValue()) {
			cir.setReturnValue(true);
		}
	}
}
