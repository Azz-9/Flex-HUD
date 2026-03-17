package me.Azz_9.flex_hud.client.mixin.bossBar;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;

@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {

	@Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
	public void extractRenderState(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		if (ModulesHelper.getInstance().isEnabled.getValue() && ModulesHelper.getInstance().bossBar.enabled.getValue()) {
			ci.cancel();
		}
	}
}
