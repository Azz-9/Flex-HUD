package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigTemplate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DurabilityPing extends ModsConfigTemplate {

	public DurabilityPing(Screen parent, double scrollAmount) {
		super(Text.literal("Durability Ping"), parent, scrollAmount);
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

		addToggleButton(getCenterX(), startY, getButtonWidth(), getButtonHeight(), Text.of("Enable durability ping"), INSTANCE.durabilityPing.enabled, true,
				toggled -> INSTANCE.durabilityPing.enabled = toggled);
		addIntSlider(getCenterX(), startY + 30, getButtonWidth(), getButtonHeight(), 80, Text.of("Durability ping threshold"), INSTANCE.durabilityPing.threshold, 10, 0, 100,
				value -> INSTANCE.durabilityPing.threshold = value, 10);

		addCyclingStringButton(getCenterX(), startY + 60, getButtonWidth(), Text.of("Durability ping type"), DurabilityPingType.class, INSTANCE.durabilityPing.pingType, DurabilityPingType.Both,
				value -> INSTANCE.durabilityPing.pingType = value);

		addToggleButton(getCenterX(), startY + 90, getButtonWidth(), getButtonHeight(), Text.of("Check armor pieces durability"), INSTANCE.durabilityPing.checkArmorPieces, true,
				toggled -> INSTANCE.durabilityPing.checkArmorPieces = toggled);
		addToggleButton(getCenterX(), startY + 120, getButtonWidth(), getButtonHeight(), Text.literal("Check elytra durability ").append(Text.literal("only").formatted(Formatting.UNDERLINE, Formatting.BOLD)), INSTANCE.durabilityPing.checkElytraOnly, false,
				toggled -> INSTANCE.durabilityPing.checkElytraOnly = toggled);
	}
}
