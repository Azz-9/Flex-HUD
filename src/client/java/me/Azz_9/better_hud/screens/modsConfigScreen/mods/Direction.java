package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class Direction extends ModsConfigAbstract {

	public Direction(Screen parent, double scrollAmount) {
		super(translatable("better_hud.direction"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		if (MinecraftClient.getInstance().getLanguageManager().getLanguage().equals("fr_fr")) {
			setButtonWidth(230);
		} else {
			setButtonWidth(170);
		}

		addToggleButton(translatable("better_hud.direction.config.enable"), INSTANCE.direction.enabled, true,
				toggled -> INSTANCE.direction.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.direction.shadow, true,
				toggled -> INSTANCE.direction.shadow = toggled);

		addColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.direction.color, 0xffffff,
				color -> INSTANCE.direction.color = color);

		addToggleButton(translatable("better_hud.direction.config.show_marker"), INSTANCE.direction.showMarker, true,
				toggled -> INSTANCE.direction.showMarker = toggled);
		addToggleButton(translatable("better_hud.direction.config.show_intermediate_point"), INSTANCE.direction.showIntermediatePoint, true,
				toggled -> INSTANCE.direction.showIntermediatePoint = toggled);
		addToggleButton(translatable("better_hud.direction.config.show_xaeros_map_waypoints"), INSTANCE.direction.showXaerosMapWaypoints, true,
				toggled -> INSTANCE.direction.showXaerosMapWaypoints = toggled);
	}
}
