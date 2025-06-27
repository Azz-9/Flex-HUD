package me.Azz_9.better_hud.client.mixin.compass;

import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.LocatorBar;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocatorBar.class)
public abstract class LocatorBarMixin {

	@Inject(method = "renderBar", at = @At("HEAD"), cancellable = true)
	private void renderBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (JsonConfigHelper.getInstance().isEnabled && JsonConfigHelper.getInstance().compass.enabled && JsonConfigHelper.getInstance().compass.overrideLocatorBar) {
			ci.cancel();
		}
	}

	@Inject(method = "renderAddons", at = @At("HEAD"), cancellable = true)
	private void renderAddons(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (JsonConfigHelper.getInstance().isEnabled && JsonConfigHelper.getInstance().compass.enabled && JsonConfigHelper.getInstance().compass.overrideLocatorBar) {
			ci.cancel();
		}
	}
}
