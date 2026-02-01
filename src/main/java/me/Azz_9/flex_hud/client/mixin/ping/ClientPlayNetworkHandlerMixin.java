package me.Azz_9.flex_hud.client.mixin.ping;

import me.Azz_9.flex_hud.client.configurableModules.modules.hud.custom.Ping;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

	@Inject(method = "onPingResult", at = @At("HEAD"))
	private void onPingResult(PingResultS2CPacket packet, CallbackInfo ci) {
		Ping.ping = Util.getMeasuringTimeMs() - packet.startTime();
	}

	@Inject(method = "onEnterReconfiguration", at = @At("HEAD"))
	private void onEnterReconfiguration(EnterReconfigurationS2CPacket packet, CallbackInfo ci) {
		Ping.stopPinging();
		Ping.packetSender = null;
	}
}
