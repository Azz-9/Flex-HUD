package me.Azz_9.better_hud.client.mixin.keybind;

import me.Azz_9.better_hud.client.Better_hudClient;
import me.Azz_9.better_hud.client.screens.OptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {

	@Inject(method = "onKeyPressed", at = @At(value = "TAIL"))
	private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
		int keyCode = key.getCategory() == InputUtil.Type.KEYSYM ? key.getCode() : -1;
		int scanCode = key.getCategory() == InputUtil.Type.SCANCODE ? key.getCode() : -1;

		// open option screen
		if (Better_hudClient.openOptionScreenKeyBind.matchesKey(keyCode, scanCode)) {
			MinecraftClient.getInstance().setScreen(new OptionsScreen());
		}

	}
}
