package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.KeyboardHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.utils.KeyHandler;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {

	@Inject(method = "keyPress", at = @At(value = "HEAD"))
	private void keyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue()) {
			KeyHandler.onKey(key, action);
		}
	}
}