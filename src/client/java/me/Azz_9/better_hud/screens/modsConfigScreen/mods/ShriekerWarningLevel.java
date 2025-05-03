package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import me.Azz_9.better_hud.screens.widgets.buttons.CustomToggleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class ShriekerWarningLevel extends ModsConfigAbstract {
	public ShriekerWarningLevel(Screen parent, double scrollAmount) {
		super(translatable("better_hud.shrieker_warning_level"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(260);
		} else {
			setButtonWidth(180);
		}

		addToggleButton(translatable("better_hud.shrieker_warning_level.config.enable"), INSTANCE.shriekerWarningLevel.enabled, true,
				toggled -> INSTANCE.shriekerWarningLevel.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.shriekerWarningLevel.shadow, true,
				toggled -> INSTANCE.shriekerWarningLevel.shadow = toggled);

		CustomToggleButtonWidget toggleButton = addToggleButton(translatable("better_hud.global.config.chroma_text_color"), INSTANCE.shriekerWarningLevel.chromaColor, false,
				toggled -> INSTANCE.shriekerWarningLevel.chromaColor = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.shriekerWarningLevel.color, 0xffffff,
				color -> INSTANCE.shriekerWarningLevel.color = color, toggleButton, true));

		toggleButton = addToggleButton(translatable("better_hud.global.config.show_background"), INSTANCE.shriekerWarningLevel.drawBackground, false,
				toggled -> INSTANCE.shriekerWarningLevel.drawBackground = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.background_color"), INSTANCE.shriekerWarningLevel.backgroundColor, 0x313131,
				color -> INSTANCE.shriekerWarningLevel.backgroundColor = color, toggleButton, false));

		addToggleButton(translatable("better_hud.shrieker_warning_level.config.show_when_in_deep_dark"), INSTANCE.shriekerWarningLevel.showWhenInDeepDark, true,
				toggled -> INSTANCE.shriekerWarningLevel.showWhenInDeepDark = toggled);
	}
}
