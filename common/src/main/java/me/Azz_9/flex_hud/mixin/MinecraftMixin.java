package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.CommonClass;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

	@Inject(method = "handleKeybinds", at = @At("HEAD"))
	private void onHandleKeybinds(CallbackInfo ci) {
		CommonClass.handleKeybindsHook();
	}
}
