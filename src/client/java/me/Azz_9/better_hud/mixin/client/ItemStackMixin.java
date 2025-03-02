package me.Azz_9.better_hud.mixin.client;

import me.Azz_9.better_hud.client.interfaces.ItemDurabilityLostCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	@Inject(method = "damage(ILnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Consumer;)V", at = @At("TAIL"))
	private void onDamageServerWorld(int amount, ServerWorld world, ServerPlayerEntity player, Consumer<Item> breakCallback, CallbackInfo ci) {
		if (world != null && player != null) {
			ItemStack stack = (ItemStack) (Object) this;
			ItemDurabilityLostCallback.EVENT.invoker().onDurabilityLost(player, stack, amount);
		}
	}

}