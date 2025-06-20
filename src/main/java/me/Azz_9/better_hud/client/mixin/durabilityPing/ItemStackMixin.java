package me.Azz_9.better_hud.client.mixin.durabilityPing;

import me.Azz_9.better_hud.client.configurableMods.mods.notHud.durabilityPing.ItemDurabilityLostCallback;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	@Inject(method = "setDamage(I)V", at = @At("TAIL"))
	private void onSetDamage(int newDamage, CallbackInfo ci) {
		ItemStack stack = (ItemStack) (Object) this;
		ItemDurabilityLostCallback.EVENT.invoker().onDurabilityLost(stack, newDamage);
	}
}