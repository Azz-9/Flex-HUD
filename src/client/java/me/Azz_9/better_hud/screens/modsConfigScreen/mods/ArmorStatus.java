package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.DisplayMode;
import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class ArmorStatus extends ModsConfigAbstract {
	public ArmorStatus(Screen parent, double scrollAmount) {
		super(translatable("better_hud.armor_status"), parent, scrollAmount);
	}

	public enum DurabilityType {
		No,
		Percentage,
		Value
	}

	@Override
	protected void init() {
		super.init();

		setButtonWidth(220);

		addToggleButton(translatable("better_hud.armor_status.config.enable"), INSTANCE.armorStatus.enabled, true,
				toggled -> INSTANCE.armorStatus.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.armorStatus.shadow, true,
				toggled -> INSTANCE.armorStatus.shadow = toggled);

		addColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.armorStatus.color, 0xffffff,
				color -> INSTANCE.armorStatus.color = color);

		addToggleButton(translatable("better_hud.armor_status.config.show_helmet"), INSTANCE.armorStatus.showHelmet, true,
				toggled -> INSTANCE.armorStatus.showHelmet = toggled);
		addToggleButton(translatable("better_hud.armor_status.config.show_chestplate"), INSTANCE.armorStatus.showChestplate, true,
				toggled -> INSTANCE.armorStatus.showChestplate = toggled);
		addToggleButton(translatable("better_hud.armor_status.config.show_leggings"), INSTANCE.armorStatus.showLeggings, true,
				toggled -> INSTANCE.armorStatus.showLeggings = toggled);
		addToggleButton(translatable("better_hud.armor_status.config.show_boots"), INSTANCE.armorStatus.showBoots, true,
				toggled -> INSTANCE.armorStatus.showBoots = toggled);
		addToggleButton(translatable("better_hud.armor_status.config.show_held_item"), INSTANCE.armorStatus.showHeldItem, true,
				toggled -> INSTANCE.armorStatus.showHeldItem = toggled);
		addToggleButton(translatable("better_hud.armor_status.config.show_arrows"), INSTANCE.armorStatus.showArrowsWhenBowInHand, true,
				toggled -> INSTANCE.armorStatus.showArrowsWhenBowInHand = toggled);

		addCyclingStringButton(getCenterX(), startY + 270, getButtonWidth(), translatable("better_hud.armor_status.config.show_durability"), DurabilityType.class, INSTANCE.armorStatus.showDurability, DurabilityType.Percentage,
				value -> INSTANCE.armorStatus.showDurability = value);
		addCyclingStringButton(getCenterX(), startY + 300, getButtonWidth(), translatable("better_hud.armor_status.config.orientation"), DisplayMode.class, INSTANCE.armorStatus.displayMode, DisplayMode.Vertical,
				value -> INSTANCE.armorStatus.displayMode = value);
	}
}
