package me.Azz_9.flex_hud.client.configurableModules.modules.notHud.durabilityPing;

import me.Azz_9.flex_hud.client.configurableModules.ConfigRegistry;
import me.Azz_9.flex_hud.client.configurableModules.modules.AbstractModule;
import me.Azz_9.flex_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.CyclingButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.IntSliderEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configEntries.ToggleButtonEntry;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigEnum;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.utils.ItemUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class DurabilityPing extends AbstractModule {
	private static final Map<String, Long> lastPingTime = new HashMap<>();
	public ConfigInteger threshold = new ConfigInteger(10, "flex_hud.durability_ping.config.threshold", 0, 100); // percentage
	public ConfigEnum<PingType> pingType = new ConfigEnum<>(PingType.class, PingType.BOTH, "flex_hud.durability_ping.config.ping_type");
	public ConfigBoolean checkArmorPieces = new ConfigBoolean(true, "flex_hud.durability_ping.config.check_armor_pieces");
	public ConfigBoolean checkElytraOnly = new ConfigBoolean(false, "flex_hud.durability_ping.config.check_elytra_only");

	public DurabilityPing() {
		this.enabled.setConfigTextTranslationKey("flex_hud.durability_ping.config.enable");
		this.enabled.setDefaultValue(false);
		this.enabled.setValue(false);

		ConfigRegistry.register(getID(), "threshold", threshold);
		ConfigRegistry.register(getID(), "pingType", pingType);
		ConfigRegistry.register(getID(), "checkArmorPieces", checkArmorPieces);
		ConfigRegistry.register(getID(), "checkElytraOnly", checkElytraOnly);
	}

	@Override
	public String getID() {
		return "durability_ping";
	}

	@Override
	public Text getName() {
		return Text.translatable("flex_hud.durability_ping");
	}


	public boolean isDurabilityUnderThreshold(ItemStack stack) {
		if (stack == null || !stack.isDamageable() || stack.getMaxDamage() == 0) {
			return false;
		}

		return ItemUtils.getDurabilityPercentage(stack) < threshold.getValue();
	}

	public void pingPlayer(ItemStack stack) {

		long currentTime = System.currentTimeMillis();

		PlayerEntity player = MinecraftClient.getInstance().player;

		// 1 minute has passed since the last ping
		if (player != null && (!lastPingTime.containsKey(stack.getItem().getTranslationKey()) || currentTime - lastPingTime.get(stack.getItem().getTranslationKey()) > 60000)) {

			lastPingTime.put(stack.getItem().getTranslationKey(), currentTime);

			// play sound, display message or both based on the selected option in the config menu
			if (pingType.getValue() != PingType.SOUND) {
				Text message = Text.literal(stack.getItemName().getString().toLowerCase() + " ").append(Text.translatable("flex_hud.durability_ping.message")).formatted(Formatting.RED); // TODO améliorer le message en fr parce que la bon
				player.sendMessage(message, true);
			}
			if (pingType.getValue() != PingType.MESSAGE) {
				MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 2.0f));
			}
		}
	}

	@Override
	public AbstractConfigurationScreen getConfigScreen(Screen parent) {
		return new AbstractConfigurationScreen(getName(), parent) {
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
								.setVariable(enabled)
								.build(),
						new IntSliderEntry.Builder()
								.setIntSliderWidth(80)
								.setVariable(threshold)
								.setStep(10)
								.build(),
						new CyclingButtonEntry.Builder<PingType>()
								.setCyclingButtonWidth(80)
								.setVariable(pingType)
								.build(),
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(checkElytraOnly)
								.build()
				);
				this.addAllEntries(
						new ToggleButtonEntry.Builder()
								.setToggleButtonWidth(buttonWidth)
								.setVariable(checkArmorPieces)
								.setDependency(this.getConfigList().getLastEntry(), true)
								.build()
				);
			}
		};
	}

	public enum PingType implements Translatable {
		SOUND("flex_hud.enum.ping_type.sound"),
		MESSAGE("flex_hud.enum.ping_type.message"),
		BOTH("flex_hud.enum.ping_type.both");

		private final String translationKey;

		PingType(String translationKey) {
			this.translationKey = translationKey;
		}

		public String getTranslationKey() {
			return translationKey;
		}
	}
}
