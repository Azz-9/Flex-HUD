package me.Azz_9.flex_hud.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.util.Util;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.Azz_9.flex_hud.utils.PingUtils;


@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
	@Inject(method = "handlePongResponse", at = @At("HEAD"))
	private void onPingResult(ClientboundPongResponsePacket packet, CallbackInfo ci) {
		PingUtils.addPingValue(Util.getMillis() - packet.time());
	}

	@Inject(method = "handleConfigurationStart", at = @At("HEAD"))
	private void onEnterReconfiguration(ClientboundStartConfigurationPacket packet, CallbackInfo ci) {
		PingUtils.stopPinging();
		PingUtils.connection = null;
	}
}
