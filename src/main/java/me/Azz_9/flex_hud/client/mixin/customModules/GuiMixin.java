package me.Azz_9.flex_hud.client.mixin.customModules;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.customModules.Variables;

@Mixin(Gui.class)
public abstract class GuiMixin {

	@Inject(method = "extractRenderState", at = @At("HEAD"))
	private void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		Variables.frame();
	}
}
