package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
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

		addColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.serverAddress.color, 0xffffff,
				color -> INSTANCE.serverAddress.color = color);

		addToggleButton(translatable("better_hud.server_address.config.hide_when_offline"), INSTANCE.serverAddress.hideWhenOffline, true,
				toggled -> INSTANCE.serverAddress.hideWhenOffline = toggled);
	}
}
