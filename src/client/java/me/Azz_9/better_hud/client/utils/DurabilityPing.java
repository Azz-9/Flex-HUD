package me.Azz_9.better_hud.client.utils;

import me.Azz_9.better_hud.modMenu.ModConfig;
import me.Azz_9.better_hud.screens.modsConfigScreen.mods.DurabilityPing.DurabilityPingType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class DurabilityPing {
	public boolean enabled = true;
	public int threshold = 10; // percentage
	public DurabilityPingType pingType = DurabilityPingType.Both;
	public boolean checkArmorPieces = true;
	public boolean checkElytraOnly = false;

	private static final DurabilityPing INSTANCE = new DurabilityPing();

	private static final Map<String, Long> lastPingTime = new HashMap<>();

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
				Text message = Text.literal(stack.getItemName().getString().toLowerCase() + " durability low!").formatted(Formatting.RED);
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
}

//TODO ajouter la detection pour les tridents et pour les armures de chiens