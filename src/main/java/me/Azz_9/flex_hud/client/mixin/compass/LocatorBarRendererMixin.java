package me.Azz_9.flex_hud.client.mixin.compass;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocatorBarRenderer.class)
public abstract class LocatorBarRendererMixin {

	@Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
	private void renderBackground(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ModulesHelper.getInstance().isEnabled.getValue() &&
				ModulesHelper.getInstance().compass.enabled.getValue() &&
				ModulesHelper.getInstance().compass.overrideLocatorBar.getValue()) {
			ci.cancel();
		}
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ModulesHelper.getInstance().isEnabled.getValue() &&
				ModulesHelper.getInstance().compass.enabled.getValue() &&
				ModulesHelper.getInstance().compass.overrideLocatorBar.getValue()) {
			ci.cancel();
		}
	}
}
