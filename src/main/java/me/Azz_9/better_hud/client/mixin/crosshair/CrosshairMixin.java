package me.Azz_9.better_hud.client.mixin.crosshair;

import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class CrosshairMixin {

	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void renderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (JsonConfigHelper.getInstance().isEnabled && JsonConfigHelper.getInstance().crosshair.enabled) {
			ci.cancel();
		}
	}
}
