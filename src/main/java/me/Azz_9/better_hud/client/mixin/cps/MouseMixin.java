package me.Azz_9.better_hud.client.mixin.cps;

import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.better_hud.client.utils.cps.KeyHandler;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

	@Inject(method = "onMouseButton", at = @At(value = "HEAD"))
	private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
		if (JsonConfigHelper.getInstance().isEnabled) {
			KeyHandler.onKey(button, action);
		}
	}
}
