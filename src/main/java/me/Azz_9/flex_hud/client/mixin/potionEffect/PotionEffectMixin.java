package me.Azz_9.flex_hud.client.mixin.potionEffect;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class PotionEffectMixin {

	@Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
	private void renderStatusEffectOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ModulesHelper.getInstance().isEnabled.getValue() &&
				ModulesHelper.getInstance().potionEffect.enabled.getValue()) {
			ci.cancel();
		}
	}
}
