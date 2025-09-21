package me.Azz_9.flex_hud.client.mixin.cps;

import me.Azz_9.flex_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.flex_hud.client.utils.cps.KeyHandler;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

	@Inject(method = "onMouseButton", at = @At(value = "HEAD"))
	private void onMouseButton(long window, MouseInput input, int action, CallbackInfo ci) {
		if (JsonConfigHelper.getInstance().isEnabled) {
			KeyHandler.onKey(input.button(), action);
		}
	}
}
