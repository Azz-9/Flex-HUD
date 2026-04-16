package me.Azz_9.flex_hud.client.mixin.cps;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.utils.cps.KeyHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

	@Inject(method = "onButton", at = @At(value = "HEAD"))
	private void onMouseButton(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
		if (ModulesHelper.getInstance().isEnabled.getValue()) {
			KeyHandler.onKey(rawButtonInfo.button(), action);
		}
	}
}
