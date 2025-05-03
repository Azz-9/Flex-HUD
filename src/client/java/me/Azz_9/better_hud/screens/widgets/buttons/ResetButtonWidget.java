package me.Azz_9.better_hud.screens.widgets.buttons;

import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.util.Identifier;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class ResetButtonWidget extends TexturedButtonWidget {

	public ResetButtonWidget(int width, int height, PressAction pressAction) {
		super(0, 0, width, height, new ButtonTextures(
				Identifier.of(MOD_ID, "widgets/buttons/reset/unfocused.png"),
				Identifier.of(MOD_ID, "widgets/buttons/reset/focused.png")
		), pressAction, 20, 20);
	}
}
