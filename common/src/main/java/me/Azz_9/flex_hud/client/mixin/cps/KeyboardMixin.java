package me.Azz_9.flex_hud.client.mixin.cps;

import me.Azz_9.flex_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.flex_hud.client.utils.cps.KeyHandler;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {

	@Inject(method = "onKey", at = @At(value = "HEAD"))
	private void onKey(long window, int action, KeyInput input, CallbackInfo ci) {
		if (JsonConfigHelper.getInstance().isEnabled) {
			KeyHandler.onKey(input.key(), action);
		}
	}
}