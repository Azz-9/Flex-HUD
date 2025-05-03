package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import me.Azz_9.better_hud.screens.widgets.buttons.CustomToggleButtonWidget;
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

		CustomToggleButtonWidget toggleButton = addToggleButton(translatable("better_hud.global.config.chroma_text_color"), INSTANCE.memoryUsage.chromaColor, false,
				toggled -> INSTANCE.memoryUsage.chromaColor = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.memoryUsage.color, 0xffffff,
				color -> INSTANCE.memoryUsage.color = color, toggleButton, true));

		toggleButton = addToggleButton(translatable("better_hud.global.config.show_background"), INSTANCE.memoryUsage.drawBackground, false,
				toggled -> INSTANCE.memoryUsage.drawBackground = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.background_color"), INSTANCE.memoryUsage.backgroundColor, 0x313131,
				color -> INSTANCE.memoryUsage.backgroundColor = color, toggleButton, false));
	}
}
