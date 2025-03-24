package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import me.Azz_9.better_hud.screens.widgets.buttons.CustomToggleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class DurabilityPing extends ModsConfigAbstract {

	public DurabilityPing(Screen parent, double scrollAmount) {
		super(translatable("better_hud.durability_ping"), parent, scrollAmount);
	}

	public enum DurabilityPingType {
		Sound,
		Message,
		Both
	}

	@Override
	protected void init() {
		super.init();
		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(250);
		} else {
			setButtonWidth(180);
		}

		addToggleButton(translatable("better_hud.durability_ping.config.enable"), INSTANCE.durabilityPing.enabled, true,
				toggled -> INSTANCE.durabilityPing.enabled = toggled);
		addIntSlider(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), 80, translatable("better_hud.durability_ping.config.threshold"), INSTANCE.durabilityPing.threshold, 10, 0, 100,
				value -> INSTANCE.durabilityPing.threshold = value, 10);

		addCyclingStringButton(getCenterX(), startY + 60, getButtonWidth(), translatable("better_hud.durability_ping.config.ping_type"), DurabilityPingType.class, INSTANCE.durabilityPing.pingType, DurabilityPingType.Both,
				value -> INSTANCE.durabilityPing.pingType = value);

		CustomToggleButtonWidget toggleButton = addToggleButton(translatable("better_hud.durability_ping.config.check_elytra_only"), INSTANCE.durabilityPing.checkElytraOnly, false,
				toggled -> INSTANCE.durabilityPing.checkElytraOnly = toggled);
		addDependentToggleButton(translatable("better_hud.durability_ping.config.check_armor_pieces"), INSTANCE.durabilityPing.checkArmorPieces, true,
				toggled -> INSTANCE.durabilityPing.checkArmorPieces = toggled, toggleButton, true);
	}
}
