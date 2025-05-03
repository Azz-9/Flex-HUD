package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import me.Azz_9.better_hud.screens.widgets.buttons.CustomToggleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class Ping extends ModsConfigAbstract {
	public Ping(Screen parent, double scrollAmount) {
		super(translatable("better_hud.ping"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(225);
		}

		addToggleButton(translatable("better_hud.ping.config.enable"), INSTANCE.ping.enabled, true,
				toggled -> INSTANCE.ping.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.ping.shadow, true,
				toggled -> INSTANCE.ping.shadow = toggled);

		CustomToggleButtonWidget toggleButton = addToggleButton(translatable("better_hud.global.config.chroma_text_color"), INSTANCE.ping.chromaColor, false,
				toggled -> INSTANCE.ping.chromaColor = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.ping.color, 0xffffff,
				color -> INSTANCE.ping.color = color, toggleButton, true));

		toggleButton = addToggleButton(translatable("better_hud.global.config.show_background"), INSTANCE.ping.drawBackground, false,
				toggled -> INSTANCE.ping.drawBackground = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.background_color"), INSTANCE.ping.backgroundColor, 0x313131,
				color -> INSTANCE.ping.backgroundColor = color, toggleButton, false));

		addToggleButton(translatable("better_hud.ping.config.hide_when_offline"), INSTANCE.ping.hideWhenOffline, true,
				toggled -> INSTANCE.ping.hideWhenOffline = toggled
		);
	}
}
