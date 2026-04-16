package me.Azz_9.flex_hud.client.utils;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public class ItemUtils {
	private static final Set<Item> ARMOR_PIECES = Set.of(
			Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET,
			Items.COPPER_BOOTS, Items.COPPER_LEGGINGS, Items.COPPER_CHESTPLATE, Items.COPPER_HELMET,
			Items.CHAINMAIL_BOOTS, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_HELMET,
			Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET,
			Items.GOLDEN_BOOTS, Items.GOLDEN_LEGGINGS, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_HELMET,
			Items.DIAMOND_BOOTS, Items.DIAMOND_LEGGINGS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET,
			Items.NETHERITE_BOOTS, Items.NETHERITE_LEGGINGS, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_HELMET,
			Items.TURTLE_HELMET, Items.ELYTRA
	);

	public static int getStackCount(@NotNull ItemStack stack, @NotNull Inventory inventory) {
		int itemCount = 0;

		for (int i = 0; i < inventory.getContainerSize(); ++i) {
			ItemStack itemStack = inventory.getItem(i);
			if ((stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION) || stack.is(Items.TIPPED_ARROW))) {
				if (itemStack.is(stack.getItem()) && Objects.equals(
						itemStack.getComponents().get(DataComponents.POTION_CONTENTS),
						stack.getComponents().get(DataComponents.POTION_CONTENTS))) {
					itemCount += itemStack.getCount();
				}

			} else if (stack.is(Items.OMINOUS_BOTTLE)) {
				if (itemStack.is(stack.getItem()) && Objects.equals(
						itemStack.getComponents().get(DataComponents.OMINOUS_BOTTLE_AMPLIFIER),
						stack.getComponents().get(DataComponents.OMINOUS_BOTTLE_AMPLIFIER))) {
					itemCount += itemStack.getCount();
				}

			} else if (stack.is(Items.FIREWORK_ROCKET)) {
				if (itemStack.is(stack.getItem()) && Objects.equals(
						itemStack.getComponents().get(DataComponents.FIREWORKS),
						stack.getComponents().get(DataComponents.FIREWORKS))) {
					itemCount += itemStack.getCount();
				}

			} else if (stack.is(Items.ENCHANTED_BOOK)) {
				if (itemStack.is(stack.getItem()) && Objects.equals(
						itemStack.getComponents().get(DataComponents.STORED_ENCHANTMENTS),
						stack.getComponents().get(DataComponents.STORED_ENCHANTMENTS))) {
					itemCount += itemStack.getCount();
				}

			} else if (stack.is(Items.LIGHT)) {
				if (itemStack.is(stack.getItem()) && Objects.equals(
						itemStack.getComponents().get(DataComponents.BLOCK_STATE),
						stack.getComponents().get(DataComponents.BLOCK_STATE))) {
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

		for (int i = 0; i < inventory.getContainerSize(); ++i) {
			ItemStack itemStack = inventory.getItem(i);
			if (itemStack.is(item)) {
				itemCount += itemStack.getCount();
			}
		}

		return itemCount;
	}

	public static double getDurabilityPercentage(@NotNull ItemStack stack) {
		return (double) getDurabilityValue(stack) / stack.getMaxDamage() * 100;
	}

	public static int getDurabilityValue(@NotNull ItemStack stack) {
		return stack.getMaxDamage() - stack.getDamageValue();
	}

	public static boolean isArmorPiece(ItemStack stack) {
		return ARMOR_PIECES.contains(stack.getItem());
	}
}
