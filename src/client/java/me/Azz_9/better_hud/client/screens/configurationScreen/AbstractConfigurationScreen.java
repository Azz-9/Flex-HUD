package me.Azz_9.better_hud.client.screens.configurationScreen;

import me.Azz_9.better_hud.client.screens.AbstractCallbackScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class AbstractConfigurationScreen extends AbstractCallbackScreen {

	private final int buttonWidth;
	private final int buttonHeight;
	private final int buttonGap;

	private ScrollableConfigList configList;

	protected AbstractConfigurationScreen(Text title, Screen parent, int buttonWidth, int buttonHeight, int buttonGap) {
		super(title, parent, Text.translatable("better_hud.global.config.callback.message_title"), Text.translatable("better_hud.global.config.callback.message_content"));
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.buttonGap = buttonGap;
	}

	protected class ToggleButton {
		//TODO
	}
}
