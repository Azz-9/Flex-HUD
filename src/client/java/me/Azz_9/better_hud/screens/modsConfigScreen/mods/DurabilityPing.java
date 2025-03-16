package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class DurabilityPing extends ModsConfigAbstract {

	public DurabilityPing(Screen parent, double scrollAmount) {
		super(Text.translatable("better_hud.durability_ping"), parent, scrollAmount);
	}

	public enum DurabilityPingType {
		Sound,
		Message,
		Both
	}

	@Override
	protected void init() {
		super.init();

		setButtonWidth(220);

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.durability_ping.config.enable"), INSTANCE.durabilityPing.enabled, true,
				toggled -> INSTANCE.durabilityPing.enabled = toggled);
		addIntSlider(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), 80, Text.translatable("better_hud.durability_ping.config.threshold"), INSTANCE.durabilityPing.threshold, 10, 0, 100,
				value -> INSTANCE.durabilityPing.threshold = value, 10);

		addCyclingStringButton(getCenterX(), startY + 60, getButtonWidth(), Text.translatable("better_hud.durability_ping.config.ping_type"), DurabilityPingType.class, INSTANCE.durabilityPing.pingType, DurabilityPingType.Both,
				value -> INSTANCE.durabilityPing.pingType = value);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.durability_ping.config.check_armor_pieces"), INSTANCE.durabilityPing.checkArmorPieces, true,
				toggled -> INSTANCE.durabilityPing.checkArmorPieces = toggled);
		addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), Text.translatable("better_hud.durability_ping.config.check_elytra_only"), INSTANCE.durabilityPing.checkElytraOnly, false,
				toggled -> INSTANCE.durabilityPing.checkElytraOnly = toggled);
	}
}
