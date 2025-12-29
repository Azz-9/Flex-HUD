package me.Azz_9.flex_hud.client.mixin.compass;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class)
public abstract class GuiMixin {

	@Inject(method = "willPrioritizeExperienceInfo", at = @At("RETURN"), cancellable = true)
	private void willPrioritizeExperienceInfo(CallbackInfoReturnable<Boolean> cir) {
		if (ModulesHelper.getInstance().isEnabled.getValue() &&
				ModulesHelper.getInstance().compass.enabled.getValue() &&
				ModulesHelper.getInstance().compass.overrideLocatorBar.getValue()) {
			cir.setReturnValue(true);
		}
	}
}
