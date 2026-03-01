package me.Azz_9.flex_hud.client.mixin;

import static me.Azz_9.flex_hud.client.Flex_hudClient.CLIENT;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.resource.ReloadableResourceManagerImpl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.customModules.Variables;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Inject(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/resource/ReloadableResourceManagerImpl;registerReloader(Lnet/minecraft/resource/ResourceReloader;)V",
					shift = At.Shift.AFTER
			)
	)
	private void initVariables(RunArgs args, CallbackInfo ci) {
		// init variables when the languages are loaded
		((ReloadableResourceManagerImpl) CLIENT.getResourceManager()).registerReloader(
				(synchronizer, manager, prepareExecutor, applyExecutor) ->
						synchronizer.whenPrepared(null).thenRunAsync(() -> {
							Variables.init();
							ModulesHelper.recompileCustomModules();
						}, applyExecutor)
		);
	}
}
