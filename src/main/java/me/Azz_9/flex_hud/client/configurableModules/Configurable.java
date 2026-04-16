package me.Azz_9.flex_hud.client.configurableModules;

import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Configurable extends Activable {
	Component getName();

	String getID();

	default @Nullable Tooltip getTooltip() {
		return null;
	}

	AbstractConfigurationScreen getConfigScreen(Screen parent);

	default List<String> getKeywords() {
		return List.of(
				getName().getString().toLowerCase(),
				getID().toLowerCase()
		);
	}
}
