package me.Azz_9.flex_hud.client.configurableModules;

import me.Azz_9.flex_hud.client.screens.configurationScreen.AbstractConfigurationScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Configurable extends Activable {
	Text getName();

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
