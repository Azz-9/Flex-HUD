package me.Azz_9.better_hud.client.configurableMods.mods.notHud;

import me.Azz_9.better_hud.client.configurableMods.mods.Mod;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DurabilityPing extends Mod {
	private static final Set<Item> ARMOR_PIECES = Set.of(
			Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET,
			Items.CHAINMAIL_BOOTS, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_HELMET,
			Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET,
			Items.GOLDEN_BOOTS, Items.GOLDEN_LEGGINGS, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_HELMET,
			Items.DIAMOND_BOOTS, Items.DIAMOND_LEGGINGS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET,
			Items.NETHERITE_BOOTS, Items.NETHERITE_LEGGINGS, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_HELMET,
			Items.TURTLE_HELMET, Items.ELYTRA
	);
	private static Map<String, Long> lastPingTime;
	public int threshold = 10; // percentage
	public PingType pingType = PingType.BOTH;
	public boolean checkArmorPieces = true;
	public boolean checkElytraOnly = false;

	public DurabilityPing() {
		lastPingTime = new HashMap<>();
	}

	public boolean isElytra(ItemStack stack) {
		return (stack.getItem() == Items.ELYTRA);
	}

	public boolean isArmorPiece(ItemStack stack) {
		return ARMOR_PIECES.contains(stack.getItem());
	}

	public boolean isDurabilityUnderThreshold(ItemStack stack) {
		if (stack == null || !stack.isDamageable() || stack.getMaxDamage() == 0) {
			return false;
		}

		double durabilityLeft = stack.getMaxDamage() - stack.getDamage();
		double percentageLeft = (durabilityLeft / stack.getMaxDamage()) * 100.0f;

		return percentageLeft < threshold;
	}

	public void pingPlayer(PlayerEntity player, ItemStack stack) {

		long currentTime = System.currentTimeMillis();

		// 1 minute has passed since the last ping
		if (!lastPingTime.containsKey(stack.getItem().getTranslationKey()) || currentTime - lastPingTime.get(stack.getItem().getTranslationKey()) > 60000) {

			lastPingTime.put(stack.getItem().getTranslationKey(), currentTime);

			// play sound, display message or both based on the selected option in the config menu
			if (pingType != PingType.SOUND) {
				Text message = Text.literal(stack.getItemName().getString().toLowerCase() + " ").append(Text.translatable("better_hud.durability_ping.message")).formatted(Formatting.RED); // TODO améliorer le message en fr parce que la bon
				player.sendMessage(message, true);
			}
			if (pingType != PingType.MESSAGE) {
				player.playSoundToPlayer(SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 2.0f);
			}
		}
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(Text.translatable("better_hud.durability_ping"), parent, 200, 20, 10) {
			@Override
			protected void init() {

			}
		};
	}

	public enum PingType {
		SOUND,
		MESSAGE,
		BOTH
	}
}
