package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import me.Azz_9.better_hud.screens.widgets.buttons.CustomToggleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class CPS extends ModsConfigAbstract {

	public CPS(Screen parent, double scrollAmount) {
		super(translatable("better_hud.cps"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(190);
		}

		addToggleButton(translatable("better_hud.cps.config.enable"), INSTANCE.cps.enabled, true,
				toggled -> INSTANCE.cps.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.cps.shadow, true,
				toggled -> INSTANCE.cps.shadow = toggled);

		CustomToggleButtonWidget toggleButton = addToggleButton(translatable("better_hud.global.config.chroma_text_color"), INSTANCE.cps.chromaColor, false,
				toggled -> INSTANCE.cps.chromaColor = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.cps.color, 0xffffff,
				color -> INSTANCE.cps.color = color, toggleButton, true));

		toggleButton = addToggleButton(translatable("better_hud.global.config.show_background"), INSTANCE.cps.drawBackground, false,
				toggled -> INSTANCE.cps.drawBackground = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.background_color"), INSTANCE.cps.backgroundColor, 0x313131,
				color -> INSTANCE.cps.backgroundColor = color, toggleButton, false));

		addToggleButton(translatable("better_hud.cps.config.show_left_click"), INSTANCE.cps.showLeftClick, true,
				toggled -> INSTANCE.cps.showLeftClick = toggled);
		addToggleButton(translatable("better_hud.cps.config.show_right_click"), INSTANCE.cps.showRightClick, true,
				toggled -> INSTANCE.cps.showRightClick = toggled);
	}
}
