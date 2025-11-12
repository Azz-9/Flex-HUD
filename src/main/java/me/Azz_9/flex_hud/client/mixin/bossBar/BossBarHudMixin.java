package me.Azz_9.flex_hud.client.mixin.bossBar;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
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
		if (ModulesHelper.getInstance().isEnabled.getValue() && ModulesHelper.getInstance().bossBar.enabled.getValue()) {
			ci.cancel();
		}
	}
}
