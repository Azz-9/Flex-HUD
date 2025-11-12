package me.Azz_9.flex_hud.client.mixin.cps;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.utils.cps.KeyHandler;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {

	@Inject(method = "onKey", at = @At(value = "HEAD"))
	private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if (ModulesHelper.getInstance().isEnabled.getValue()) {
			KeyHandler.onKey(key, action);
		}
	}
}