package me.Azz_9.better_hud.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.Azz_9.better_hud.client.screens.OptionsScreen;

public class ModMenuCompat implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return OptionsScreen::new;
	}
}
