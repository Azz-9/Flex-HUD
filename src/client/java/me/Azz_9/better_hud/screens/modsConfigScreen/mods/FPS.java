package me.Azz_9.better_hud.screens.modsConfigScreen.mods;

import me.Azz_9.better_hud.screens.modsConfigScreen.ModsConfigAbstract;
import me.Azz_9.better_hud.screens.widgets.buttons.CustomToggleButtonWidget;
import net.minecraft.client.gui.screen.Screen;

import static net.minecraft.text.Text.translatable;

public class FPS extends ModsConfigAbstract {
	public FPS(Screen parent, double scrollAmount) {
		super(translatable("better_hud.fps"), parent, scrollAmount);
	}

	@Override
	protected void init() {
		super.init();

		addToggleButton(translatable("better_hud.fps.config.enable"), INSTANCE.fps.enabled, true,
				toggled -> INSTANCE.fps.enabled = toggled);
		addToggleButton(translatable("better_hud.global.config.text_shadow"), INSTANCE.fps.shadow, true,
				toggled -> INSTANCE.fps.shadow = toggled);

		CustomToggleButtonWidget toggleButton = addToggleButton(translatable("better_hud.global.config.chroma_text_color"), INSTANCE.fps.chromaColor, false,
				toggled -> INSTANCE.fps.chromaColor = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.text_color"), INSTANCE.fps.color, 0xffffff,
				color -> INSTANCE.fps.color = color, toggleButton, true));

		toggleButton = addToggleButton(translatable("better_hud.global.config.show_background"), INSTANCE.fps.drawBackground, false,
				toggled -> INSTANCE.fps.drawBackground = toggled);
		toggleButton.addDependents(addDependentColorButton(translatable("better_hud.global.config.background_color"), INSTANCE.fps.backgroundColor, 0x313131,
				color -> INSTANCE.fps.backgroundColor = color, toggleButton, false));
	}
}
