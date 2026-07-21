package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.modules.Modules;

@Mixin(LocatorBarRenderer.class)
public abstract class LocatorBarRendererMixin {

	@Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
	private void renderBackground(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue() &&
				Modules.getInstance().compass.enabled.getValue() &&
				Modules.getInstance().compass.overrideLocatorBar.getValue()) {
			ci.cancel();
		}
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue() &&
				Modules.getInstance().compass.enabled.getValue() &&
				Modules.getInstance().compass.overrideLocatorBar.getValue()) {
			ci.cancel();
		}
	}
}
