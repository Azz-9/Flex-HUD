package me.Azz_9.flex_hud.client.mixin.reach;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.tickables.ReachTickable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

	@Inject(method = "attackEntity", at = @At("HEAD"))
	private void onAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
		if (!ModulesHelper.getInstance().isEnabled.getValue() || !ModulesHelper.getInstance().reach.enabled.getValue()) {
			return;
		}

		if (target.isAttackable()) {
			if (!target.handleAttack(player)) {
				if (MinecraftClient.getInstance().player != null && player.getUuid().equals(MinecraftClient.getInstance().player.getUuid())) {
					ReachTickable.calculateReach(player, target);
				}
			}
		}
	}
}
