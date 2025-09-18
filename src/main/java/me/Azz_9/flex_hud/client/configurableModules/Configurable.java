package me.Azz_9.flex_hud.client.configurableModules;

import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public interface Configurable {
	Text getName();

	String getID();

	AbstractConfigurationScreen getConfigScreen(Screen parent);
}
