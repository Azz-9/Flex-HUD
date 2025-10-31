package me.Azz_9.flex_hud.client.mixin.keybind;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.screens.OptionsScreen;
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
		// open option screen
		if (Flex_hudClient.openOptionScreenKeyBind.isPressed()) {
			MinecraftClient.getInstance().setScreen(new OptionsScreen());
		}
	}
}
