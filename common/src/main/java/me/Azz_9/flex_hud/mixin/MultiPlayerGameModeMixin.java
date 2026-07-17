package me.Azz_9.flex_hud.mixin;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.client.modules.Modules;
import me.Azz_9.flex_hud.client.tickables.ReachTickable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

	@Inject(
			method = "attack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V",
					ordinal = 0
			)
	)
	private void onAttack(Player player, Entity entity, CallbackInfo ci) {
		if (!Modules.getInstance().isEnabled.getValue() || !Modules.getInstance().reach.enabled.getValue()) {
			return;
		}

		if (entity.isAttackable()) {
			if (!entity.skipAttackInteraction(player)) {
				if (MINECRAFT.player != null && player.getUUID().equals(MINECRAFT.player.getUUID())) {
					ReachTickable.calculateReach(player, entity);
				}
			}
		}
	}
}