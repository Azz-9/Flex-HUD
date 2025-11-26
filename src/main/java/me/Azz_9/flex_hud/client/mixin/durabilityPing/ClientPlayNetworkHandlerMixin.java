package me.Azz_9.flex_hud.client.mixin.durabilityPing;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.DurabilityPing;
import me.Azz_9.flex_hud.client.utils.ItemUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

	@Unique
	private static long lastProcessedTick = -1;

	@Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"))
	private void onSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
		if (!ModulesHelper.getInstance().isEnabled.getValue() || !ModulesHelper.getInstance().durabilityPing.isEnabled())
			return;

		MinecraftClient client = MinecraftClient.getInstance();
		ItemStack stack = packet.getStack();
		if (client.world == null || client.player == null || stack == null || stack.isEmpty() || !stack.isDamageable())
			return;

		long tick = client.world.getTime();

		int dmg = stack.getDamage();
		int old = client.player.getInventory().getStack(packet.getSlot()).getDamage();

		if (tick == lastProcessedTick || dmg == old) {
			return;

		} else {
			DurabilityPing durabilityPing = ModulesHelper.getInstance().durabilityPing;
			if (durabilityPing.isDurabilityUnderThreshold(stack) &&
					((durabilityPing.checkElytraOnly.getValue() && stack.isOf(Items.ELYTRA)) || // check only elytra and item is elytra
							(ItemUtils.isArmorPiece(stack) && durabilityPing.checkArmorPieces.getValue()) || // item is armor and check armor piece
							(!durabilityPing.checkElytraOnly.getValue() && !ItemUtils.isArmorPiece(stack)))) { // not check only elytra and item is not armor piece

				durabilityPing.pingPlayer(stack);
			}
		}

		lastProcessedTick = tick;
	}
}
