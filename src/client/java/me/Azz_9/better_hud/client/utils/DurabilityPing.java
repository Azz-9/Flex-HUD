package me.Azz_9.better_hud.client.utils;

import me.Azz_9.better_hud.screens.modsConfigScreen.mods.DurabilityPing.DurabilityPingType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DurabilityPing {
	public boolean enabled = true;
	public int threshold = 10; // percentage
	public DurabilityPingType pingType = DurabilityPingType.Both;
	public boolean checkArmorPieces = true;
	public boolean checkElytraOnly = false;

	private static final DurabilityPing INSTANCE = new DurabilityPing();

	private static final Map<String, Long> lastPingTime = new HashMap<>();

	private static final Set<Item> ARMOR_PIECES = new HashSet<>(Set.of(
			Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET,
			Items.CHAINMAIL_BOOTS, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_HELMET,
			Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET,
			Items.GOLDEN_BOOTS, Items.GOLDEN_LEGGINGS, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_HELMET,
			Items.DIAMOND_BOOTS, Items.DIAMOND_LEGGINGS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET,
			Items.NETHERITE_BOOTS, Items.NETHERITE_LEGGINGS, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_HELMET,
			Items.TURTLE_HELMET, Items.ELYTRA
	));

	public static boolean isDurabilityUnderThreshold(ItemStack stack) {
		if (stack == null || !stack.isDamageable() || stack.getMaxDamage() == 0) {
			return false;
		}

		double durabilityLeft = stack.getMaxDamage() - stack.getDamage();
		double percentageLeft = (durabilityLeft / stack.getMaxDamage()) * 100.0f;

		return percentageLeft < INSTANCE.threshold;
	}

	public static void pingPlayer(PlayerEntity player, ItemStack stack) {

		long currentTime = System.currentTimeMillis();

		// 1 minute has passed since the last ping
		if (!lastPingTime.containsKey(stack.getItem().getTranslationKey()) || currentTime - lastPingTime.get(stack.getItem().getTranslationKey()) > 60000) {

			lastPingTime.put(stack.getItem().getTranslationKey(), currentTime);

			// play sound, display message or both based on the selected option in the config menu
			if (INSTANCE.pingType != DurabilityPingType.Sound) {
				Text message = Text.literal(stack.getItemName().getString().toLowerCase() + " ").append(Text.translatable("better_hud.durability_ping.message")).formatted(Formatting.RED); // TODO améliorer le message en fr parce que la bon
				player.sendMessage(message, true);
			}
			if (INSTANCE.pingType != DurabilityPingType.Message) {
				player.playSoundToPlayer(SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 2.0f);
			}
		}
	}

	public static DurabilityPing getInstance() {
		return INSTANCE;
	}

	public static boolean isElytra(ItemStack stack) {
		return (stack.getItem() == Items.ELYTRA);
	}

	public static boolean isArmorPiece(ItemStack stack) {
		return ARMOR_PIECES.contains(stack.getItem());
	}
}

//TODO ajouter la detection pour les tridents et pour les armures de chiens