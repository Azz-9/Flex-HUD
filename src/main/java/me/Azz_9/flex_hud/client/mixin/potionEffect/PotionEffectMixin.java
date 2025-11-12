package me.Azz_9.flex_hud.client.mixin.potionEffect;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class PotionEffectMixin {

	@Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
	private void renderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (ModulesHelper.getInstance().isEnabled.getValue() && ModulesHelper.getInstance().potionEffect.enabled.getValue()) {
			ci.cancel();
		}
	}
}
