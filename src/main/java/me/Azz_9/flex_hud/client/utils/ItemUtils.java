package me.Azz_9.flex_hud.client.utils;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemUtils {
	public static int getStackCount(@NotNull ItemStack stack, @NotNull Inventory inventory) {
		int itemCount = 0;

		for (int i = 0; i < inventory.size(); ++i) {
			ItemStack itemStack = inventory.getStack(i);
			if ((stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.TIPPED_ARROW))) {
				if (itemStack.isOf(stack.getItem()) && Objects.equals(
						itemStack.getComponents().get(DataComponentTypes.POTION_CONTENTS),
						stack.getComponents().get(DataComponentTypes.POTION_CONTENTS))) {
					itemCount += itemStack.getCount();
				}

			} else if (stack.isOf(Items.OMINOUS_BOTTLE)) {
				if (itemStack.isOf(stack.getItem()) && Objects.equals(
						itemStack.getComponents().get(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER),
						stack.getComponents().get(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER))) {
					itemCount += itemStack.getCount();
				}

			} else if (stack.isOf(Items.FIREWORK_ROCKET)) {
				if (itemStack.isOf(stack.getItem()) && Objects.equals(
						itemStack.getComponents().get(DataComponentTypes.FIREWORKS),
						stack.getComponents().get(DataComponentTypes.FIREWORKS))) {
					itemCount += itemStack.getCount();
				}

			} else if (stack.isOf(Items.ENCHANTED_BOOK)) {
				if (itemStack.isOf(stack.getItem()) && Objects.equals(
						itemStack.getComponents().get(DataComponentTypes.STORED_ENCHANTMENTS),
						stack.getComponents().get(DataComponentTypes.STORED_ENCHANTMENTS))) {
					itemCount += itemStack.getCount();
				}

			} else if (stack.isOf(Items.LIGHT)) {
				if (itemStack.isOf(stack.getItem()) && Objects.equals(
						itemStack.getComponents().get(DataComponentTypes.BLOCK_STATE),
						stack.getComponents().get(DataComponentTypes.BLOCK_STATE))) {
					itemCount += itemStack.getCount();
				}

			} else if (itemStack.getItem().equals(stack.getItem())) {
				itemCount += itemStack.getCount();
			}
		}

		return itemCount;
	}

	public static int getItemCount(@NotNull Item item, @NotNull Inventory inventory) {
		int itemCount = 0;

		for (int i = 0; i < inventory.size(); ++i) {
			ItemStack itemStack = inventory.getStack(i);
			if (itemStack.isOf(item)) {
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
