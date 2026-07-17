package me.Azz_9.flex_hud.client.config;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import me.Azz_9.flex_hud.client.gui.screens.AbstractConfigurationScreen;

public interface Configurable extends Activable {
	Component getName();

	String getID();

	default @Nullable Tooltip getTooltip() {
		return null;
	}

	AbstractConfigurationScreen getConfigScreen(Screen parent);

	default List<String> getKeywords() {
		return Lists.newArrayList(
				getName().getString().toLowerCase(),
				getID().toLowerCase()
		);
	}
}
