package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.utils.KeyHandler;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {

	@Inject(method = "keyPress", at = @At(value = "HEAD"))
	private void keyPress(long handle, int action, KeyEvent event, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue()) {
			KeyHandler.onKey(event.key(), action);
		}
	}
}