package me.Azz_9.flex_hud.client.mixin.durabilityPing;

import me.Azz_9.flex_hud.client.configurableModules.ModulesHelper;
import me.Azz_9.flex_hud.client.configurableModules.modules.notHud.DurabilityPing;
import me.Azz_9.flex_hud.client.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin {

	@Unique
	private static long lastProcessedTick = -1;

	@Inject(method = "handleContainerSetSlot", at = @At("HEAD"))
	private void onSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
		if (!ModulesHelper.getInstance().isEnabled.getValue() || !ModulesHelper.getInstance().durabilityPing.isEnabled())
			return;

		Minecraft minecraft = Minecraft.getInstance();
		ItemStack stack = packet.getItem();
		if (minecraft.level == null || minecraft.player == null || stack.isEmpty() || !stack.isDamageableItem())
			return;

		long tick = minecraft.level.getGameTime();

		int dmg = stack.getDamageValue();
		int old = minecraft.player.getInventory().getItem(packet.getSlot()).getDamageValue();

		if (tick == lastProcessedTick || dmg == old) {
			return;

		} else {
			DurabilityPing durabilityPing = ModulesHelper.getInstance().durabilityPing;
			if (durabilityPing.isDurabilityUnderThreshold(stack) &&
					((durabilityPing.checkElytraOnly.getValue() && stack.is(Items.ELYTRA)) || // check only elytra and item is elytra
							(ItemUtils.isArmorPiece(stack) && durabilityPing.checkArmorPieces.getValue()) || // item is armor and check armor piece
							(!durabilityPing.checkElytraOnly.getValue() && !ItemUtils.isArmorPiece(stack)))) { // not check only elytra and item is not armor piece

				durabilityPing.pingPlayer(stack);
			}
		}

		lastProcessedTick = tick;
	}
}
