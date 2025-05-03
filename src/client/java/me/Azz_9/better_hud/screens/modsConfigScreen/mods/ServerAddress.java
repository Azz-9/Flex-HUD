package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import me.Azz_9.better_hud.screens.widgets.buttons.CustomToggleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class ServerAddress extends ModsConfigAbstract {
	public ServerAddress(Screen parent, double scrollAmount) {
		super(translatable("better_hud.server_address"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(225);
		}

		addToggleButton(translatable("better_hud.server_address.config.enable"), INSTANCE.serverAddress.enabled, true,
				toggled -> INSTANCE.serverAddress.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.serverAddress.shadow, true,
				toggled -> INSTANCE.serverAddress.shadow = toggled);

		CustomToggleButtonWidget toggleButton = addToggleButton(translatable("better_hud.global.config.chroma_text_color"), INSTANCE.serverAddress.chromaColor, false,
				toggled -> INSTANCE.serverAddress.chromaColor = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.serverAddress.color, 0xffffff,
				color -> INSTANCE.serverAddress.color = color, toggleButton, true));

		toggleButton = addToggleButton(translatable("better_hud.global.config.show_background"), INSTANCE.serverAddress.drawBackground, false,
				toggled -> INSTANCE.serverAddress.drawBackground = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.background_color"), INSTANCE.serverAddress.backgroundColor, 0x313131,
				color -> INSTANCE.serverAddress.backgroundColor = color, toggleButton, false));

		addToggleButton(translatable("better_hud.server_address.config.hide_when_offline"), INSTANCE.serverAddress.hideWhenOffline, true,
				toggled -> INSTANCE.serverAddress.hideWhenOffline = toggled);
	}
}
