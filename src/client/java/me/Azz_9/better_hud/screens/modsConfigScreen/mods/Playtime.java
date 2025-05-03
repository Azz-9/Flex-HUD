package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import me.Azz_9.better_hud.screens.widgets.buttons.CustomToggleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class Playtime extends ModsConfigAbstract {
	public Playtime(Screen parent, double scrollAmount) {
		super(translatable("better_hud.playtime"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(155);
		}

		addToggleButton(translatable("better_hud.playtime.config.enable"), INSTANCE.playtime.enabled, true,
				toggled -> INSTANCE.playtime.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.playtime.shadow, true,
				toggled -> INSTANCE.playtime.shadow = toggled);

		CustomToggleButtonWidget toggleButton = addToggleButton(translatable("better_hud.global.config.chroma_text_color"), INSTANCE.playtime.chromaColor, false,
				toggled -> INSTANCE.playtime.chromaColor = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.playtime.color, 0xffffff,
				color -> INSTANCE.playtime.color = color, toggleButton, true));

		toggleButton = addToggleButton(translatable("better_hud.global.config.show_background"), INSTANCE.playtime.drawBackground, false,
				toggled -> INSTANCE.playtime.drawBackground = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.background_color"), INSTANCE.playtime.backgroundColor, 0x313131,
				color -> INSTANCE.playtime.backgroundColor = color, toggleButton, false));

		addToggleButton(translatable("better_hud.playtime.config.show_prefix"), INSTANCE.playtime.showPrefix, true,
				toggled -> INSTANCE.playtime.showPrefix = toggled);
	}
}
