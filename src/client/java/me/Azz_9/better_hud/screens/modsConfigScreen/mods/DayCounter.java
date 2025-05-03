package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import me.Azz_9.better_hud.screens.widgets.buttons.CustomToggleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class DayCounter extends ModsConfigAbstract {

	public DayCounter(Screen parent, double scrollAmount) {
		super(translatable("better_hud.day_counter"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(160);
		}

		addToggleButton(translatable("better_hud.day_counter.config.enable"), INSTANCE.dayCounter.enabled, true,
				toggled -> INSTANCE.dayCounter.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.dayCounter.shadow, true,
				toggled -> INSTANCE.dayCounter.shadow = toggled);

		CustomToggleButtonWidget toggleButton = addToggleButton(translatable("better_hud.global.config.chroma_text_color"), INSTANCE.cps.chromaColor, false,
				toggled -> INSTANCE.cps.chromaColor = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.cps.color, 0xffffff,
				color -> INSTANCE.cps.color = color, toggleButton, true));

		toggleButton = addToggleButton(translatable("better_hud.global.config.show_background"), INSTANCE.dayCounter.drawBackground, false,
				toggled -> INSTANCE.dayCounter.drawBackground = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.background_color"), INSTANCE.dayCounter.backgroundColor, 0x313131,
				color -> INSTANCE.dayCounter.backgroundColor = color, toggleButton, false));
	}
}
