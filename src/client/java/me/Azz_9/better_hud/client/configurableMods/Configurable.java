package me.Azz_9.better_hud.client.configurableMods;

import me.Azz_9.better_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import net.minecraft.client.gui.screen.Screen;

public interface Configurable {
	AbstractConfigurationScreen getConfigScreen(Screen parent);
}
