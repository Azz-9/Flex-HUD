package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class MemoryUsage extends ModsConfigAbstract {

	public MemoryUsage(Screen parent, double scrollAmount) {
		super(translatable("better_hud.memory_usage"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(200);
		}

		addToggleButton(translatable("better_hud.memory_usage.config.enable"), INSTANCE.memoryUsage.enabled, true,
				toggled -> INSTANCE.memoryUsage.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.memoryUsage.shadow, true,
				toggled -> INSTANCE.memoryUsage.shadow = toggled);

		addColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.memoryUsage.color, 0xffffff,
				color -> INSTANCE.memoryUsage.color = color);
	}
}
