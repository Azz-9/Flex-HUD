package me.Azz_9.flex_hud.client.configurableModules.modules.hud;

import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigBoolean;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBackgroundElement extends AbstractHudElement {
	protected transient final int BACKGROUND_PADDING = 2;

	public ConfigBoolean drawBackground = new ConfigBoolean(false, "flex_hud.global.config.show_background");
	public ConfigInteger backgroundColor = new ConfigInteger(0x313131, "flex_hud.global.config.background_color");

	public AbstractBackgroundElement(double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);
	}

	protected void drawBackground(DrawContext context) {
		if (drawBackground.getValue() && width != 0 && height != 0) {
			context.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, width + BACKGROUND_PADDING, height + BACKGROUND_PADDING, getBackgroundColor());
		}
	}

	protected int getBackgroundColor() {
		return 0x7f000000 | backgroundColor.getValue();
	}
}
