package me.Azz_9.flex_hud.mixin;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.CommonClass;
import me.Azz_9.flex_hud.client.gui.screens.OptionsScreen;

// lower priority to make it apply before the mixin from toomanyshortcuts TODO this is bad
@Mixin(value = KeyMapping.class, priority = 900)
public abstract class KeyBindingMixin {

	@Inject(method = "click", at = @At(value = "HEAD"))
	private static void onKeyPressed(InputConstants.Key key, CallbackInfo ci) {
		// open option screen
		if (CommonClass.openOptionScreenKeyBind.isDown()) {
			MINECRAFT.gui.setScreen(new OptionsScreen());
		}
	}
}
