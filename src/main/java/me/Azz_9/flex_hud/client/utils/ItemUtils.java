package me.Azz_9.flex_hud.client.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemUtils {
	public static int getStackCount(@NotNull ItemStack stack, @NotNull PlayerEntity player) {
		int itemCount = 0;

		for (int i = 0; i < player.getInventory().size(); ++i) {
			ItemStack itemStack = player.getInventory().getStack(i);
			if (itemStack.getItem().equals(stack.getItem()) && itemStack.getComponents().equals(stack.getComponents())) {
				itemCount += itemStack.getCount();
			}
		}

		return itemCount;
	}

	public static double getDurabilityPercentage(@NotNull ItemStack stack) {
		return (double) getDurabilityValue(stack) / stack.getMaxDamage() * 100;
	}

	public static int getDurabilityValue(@NotNull ItemStack stack) {
		return stack.getMaxDamage() - stack.getDamage();
	}
}
