package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.DisplayMode;
import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ArmorStatus extends ModsConfigTemplate {
	public ArmorStatus(Screen parent, double scrollAmount) {
		super(Text.literal("Armor Status"), parent, scrollAmount);
	}

	public enum DurabilityType {
		No,
		Percentage,
		Value
	}

	@Override
	protected void init() {
		super.init();

		setButtonWidth(250);

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show armor status"), INSTANCE.armorStatus.enabled, true,
				toggled -> INSTANCE.armorStatus.enabled = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.armorStatus.shadow, true,
				toggled -> INSTANCE.armorStatus.shadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.armorStatus.color, 0xffffff,
				color -> INSTANCE.armorStatus.color = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Show helmet"), INSTANCE.armorStatus.showHelmet, true,
				toggled -> INSTANCE.armorStatus.showHelmet = toggled);
        addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), Text.of("Show chestplate"), INSTANCE.armorStatus.showChestplate, true,
				toggled -> INSTANCE.armorStatus.showChestplate = toggled);
		addToggleButton(getCenterX(), startY + 150, getButtonWidth(), getButtonHeight(), Text.of("Show leggings"), INSTANCE.armorStatus.showLeggings, true,
                toggled -> INSTANCE.armorStatus.showLeggings = toggled);
		addToggleButton(getCenterX(), startY + 180, getButtonWidth(), getButtonHeight(), Text.of("Show boots"), INSTANCE.armorStatus.showBoots, true,
                toggled -> INSTANCE.armorStatus.showBoots = toggled);
		addToggleButton(getCenterX(), startY + 210, getButtonWidth(), getButtonHeight(), Text.of("Show held item"), INSTANCE.armorStatus.showHeldItem, true,
				toggled -> INSTANCE.armorStatus.showHeldItem = toggled);
		addToggleButton(getCenterX(), startY + 240, getButtonWidth(), getButtonHeight(), Text.of("Show arrows when held item is a bow"), INSTANCE.armorStatus.showArrowsWhenBowInHand, true,
				toggled -> INSTANCE.armorStatus.showArrowsWhenBowInHand = toggled);

		addCyclingStringButton(getCenterX(), startY + 270, getButtonWidth(), Text.of("Show durability"), DurabilityType.class, INSTANCE.armorStatus.showDurability, DurabilityType.Percentage,
				value -> INSTANCE.armorStatus.showDurability = value);
		addCyclingStringButton(getCenterX(), startY + 300, getButtonWidth(), Text.of("Orientation"), DisplayMode.class, INSTANCE.armorStatus.displayMode, DisplayMode.Vertical,
				value -> INSTANCE.armorStatus.displayMode = value);
	}
}
