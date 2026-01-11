package me.Azz_9.flex_hud.client.mixin.reach;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.tickables.ReachTickable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

	@Inject(method = "attack", at = @At("HEAD"))
	private void onAttack(Player player, Entity target, CallbackInfo ci) {
		if (!ModulesHelper.getInstance().isEnabled.getValue() || !ModulesHelper.getInstance().reach.enabled.getValue()) {
			return;
		}

		if (target.isAttackable()) {
			if (!target.skipAttackInteraction(player)) {
				if (Minecraft.getInstance().player != null && player.getUUID().equals(Minecraft.getInstance().player.getUUID())) {
					ReachTickable.calculateReach(player, target);
				}
			}
		}
	}
}