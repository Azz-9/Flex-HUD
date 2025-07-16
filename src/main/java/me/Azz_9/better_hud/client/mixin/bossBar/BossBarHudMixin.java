package me.Azz_9.better_hud.client.mixin.bossBar;

import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(DrawContext context, CallbackInfo ci) {
		if (JsonConfigHelper.getInstance().isEnabled) {
			ci.cancel();
		}
	}
}
