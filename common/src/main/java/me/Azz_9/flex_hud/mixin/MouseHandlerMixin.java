package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.MouseHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.utils.KeyHandler;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

	@Inject(method = "onPress", at = @At(value = "HEAD"))
	private void onMouseButton(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
		if (Modules.getInstance().isEnabled.getValue()) {
			KeyHandler.onKey(button, action);
		}
	}
}
