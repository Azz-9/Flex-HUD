package me.Azz_9.flex_hud.client.mixin.cps;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.utils.cps.KeyHandler;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {

	@Inject(method = "keyPress", at = @At(value = "HEAD"))
	private void keyPress(long handle, int action, KeyEvent event, CallbackInfo ci) {
		if (ModulesHelper.getInstance().isEnabled.getValue()) {
			KeyHandler.onKey(event.key(), action);
		}
	}
}