package me.Azz_9.better_hud.client.mixin.reach;

import me.Azz_9.better_hud.client.configurableModules.JsonConfigHelper;
import me.Azz_9.better_hud.client.utils.reach.ReachUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerMixin {

	@Inject(method = "attack", at = @At("HEAD"))
	private void onAttack(Entity target, CallbackInfo ci) {
		if (!JsonConfigHelper.getInstance().isEnabled || !JsonConfigHelper.getInstance().reach.enabled.getValue()) {
			return;
		}

		PlayerEntity player = (PlayerEntity) (Object) this;

		if (target.isAttackable()) {
			if (!target.handleAttack(player)) {
				if (MinecraftClient.getInstance().player != null && player.getUuid().equals(MinecraftClient.getInstance().player.getUuid())) {
					ReachUtils.calculateReach(player, target);
				}
			}
		}
	}
}
