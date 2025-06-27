package me.Azz_9.better_hud.client.configurableModules.modules.notHud.durabilityPing;

import me.Azz_9.better_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.better_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.IntSliderEntry;
import me.Azz_9.better_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import net.minecraft.client.MinecraftClient;
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

public class DurabilityPing extends AbstractModule {
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

	@Override
	public String getID() {
		return "durability_ping";
	}

	@Override
	public Text getName() {
		return Text.translatable("better_hud.durability_ping");
	}

	public static boolean isElytra(ItemStack stack) {
		return (stack.getItem() == Items.ELYTRA);
	}

	public static boolean isArmorPiece(ItemStack stack) {
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

	public void pingPlayer(ItemStack stack) {

		long currentTime = System.currentTimeMillis();

		PlayerEntity player = MinecraftClient.getInstance().player;

		// 1 minute has passed since the last ping
		if (player != null && (!lastPingTime.containsKey(stack.getItem().getTranslationKey()) || currentTime - lastPingTime.get(stack.getItem().getTranslationKey()) > 60000)) {

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
	public AbstractConfigurationScreen getConfigScreen(Screen parent, double parentScrollAmount) {
		return new AbstractConfigurationScreen(getName(), parent, parentScrollAmount) {
			@Override
			protected void init() {
				if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
					buttonWidth = 250;
				} else {
					buttonWidth = 180;
				}

				super.init();

				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(enabled)
								.setDefaultValue(true)
								.setOnToggle(toggled -> enabled = toggled)
								.setText(Text.translatable("better_hud.durability_ping.config.enable"))
								.build(),
						new IntSliderEntry.Builder()
								.setIntSliderWidth(80)
								.setValue(threshold)
								.setMin(0)
								.setMax(100)
								.setStep(10)
								.setDefaultValue(10)
								.setOnValueChange((value -> threshold = value))
								.setText(Text.translatable("better_hud.durability_ping.config.threshold"))
								.build(),
						new CyclingButtonEntry.Builder<PingType>()
								.setCyclingButtonWidth(80)
								.setValue(pingType)
								.setDefaultValue(PingType.BOTH)
								.setOnValueChange((value) -> pingType = value)
								.setText(Text.translatable("better_hud.durability_ping.config.ping_type"))
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(checkElytraOnly)
								.setDefaultValue(false)
								.setOnToggle((value) -> checkElytraOnly = value)
								.setText(Text.translatable("better_hud.durability_ping.config.check_elytra_only"))
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setToggled(checkArmorPieces)
								.setDefaultValue(true)
								.setOnToggle((value) -> checkArmorPieces = value)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.setText(Text.translatable("better_hud.durability_ping.config.check_armor_pieces"))
								.build()
				);
			}
		};
	}

	public enum PingType implements Translatable {
		SOUND("better_hud.enum.ping_type.sound"),
		MESSAGE("better_hud.enum.ping_type.message"),
		BOTH("better_hud.enum.ping_type.both");

		private final String translationKey;

		PingType(String translationKey) {
			this.translationKey = translationKey;
		}

		public String getTranslationKey() {
			return translationKey;
		}
	}
}
