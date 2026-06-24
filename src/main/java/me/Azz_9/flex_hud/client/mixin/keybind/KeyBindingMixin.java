package me.Azz_9.flex_hud.client.mixin.keybind;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.Flex_hudClient;
import me.Azz_9.flex_hud.client.screens.OptionsScreen;

// lower priority to make it apply before the mixin from toomanyshortcuts
@Mixin(value = KeyMapping.class, priority = 900)
public abstract class KeyBindingMixin {

	@Inject(method = "click", at = @At(value = "HEAD"))
	private static void onKeyPressed(InputConstants.Key key, CallbackInfo ci) {
		// open option screen
		if (Flex_hudClient.openOptionScreenKeyBind.isDown()) {
			MINECRAFT.gui.setScreen(new OptionsScreen());
		}
	}
}
