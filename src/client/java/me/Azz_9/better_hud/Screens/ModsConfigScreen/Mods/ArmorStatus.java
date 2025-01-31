package me.Azz_9.better_hud.Screens.ModsConfigScreen.Mods;

import me.Azz_9.better_hud.Screens.ModsConfigScreen.DisplayMode;
import me.Azz_9.better_hud.Screens.ModsConfigScreen.ModsConfigTemplate;
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

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Show armor status"), INSTANCE.showArmorStatus, true,
				toggled -> INSTANCE.showArmorStatus = toggled);
		addToggleButton(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), Text.of("Text shadow"), INSTANCE.armorStatusTextShadow, true,
				toggled -> INSTANCE.armorStatusTextShadow = toggled);

		addColorButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Text color"), INSTANCE.armorStatusTextColor, 0xffffff,
				color -> INSTANCE.armorStatusTextColor = color);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Show helmet"), INSTANCE.showHelmet, true,
				toggled -> INSTANCE.showHelmet = toggled);
        addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), Text.of("Show chestplate"), INSTANCE.showChestplate, true,
				toggled -> INSTANCE.showChestplate = toggled);
		addToggleButton(getCenterX(), startY + 150, getButtonWidth(), getButtonHeight(), Text.of("Show leggings"), INSTANCE.showLeggings, true,
                toggled -> INSTANCE.showLeggings = toggled);
		addToggleButton(getCenterX(), startY + 180, getButtonWidth(), getButtonHeight(), Text.of("Show boots"), INSTANCE.showBoots, true,
                toggled -> INSTANCE.showBoots = toggled);
		addToggleButton(getCenterX(), startY + 210, getButtonWidth(), getButtonHeight(), Text.of("Show held item"), INSTANCE.showHeldItem, true,
				toggled -> INSTANCE.showHeldItem = toggled);
		addToggleButton(getCenterX(), startY + 240, getButtonWidth(), getButtonHeight(), Text.of("Show arrows when held item is a bow"), INSTANCE.showArrowsWhenBowInHand, true,
				toggled -> INSTANCE.showArrowsWhenBowInHand = toggled);

		addCyclingStringButton(getCenterX(), startY + 270, getButtonWidth(), Text.of("Show durability"), DurabilityType.class, INSTANCE.showDurability, DurabilityType.Percentage,
				value -> INSTANCE.showDurability = value);
		addCyclingStringButton(getCenterX(), startY + 300, getButtonWidth(), Text.of("Orientation"), DisplayMode.class, INSTANCE.displayModeArmorStatus, DisplayMode.Vertical,
				value -> INSTANCE.displayModeArmorStatus = value);
	}
}
