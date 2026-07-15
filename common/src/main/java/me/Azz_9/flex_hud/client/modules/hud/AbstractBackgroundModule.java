package me.Azz_9.flex_hud.client.modules.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.config.option.ConfigInteger;

public abstract class AbstractBackgroundModule extends AbstractMovableModule {
	protected transient final int BACKGROUND_PADDING = 2;

	public ConfigBoolean drawBackground = new ConfigBoolean(false, "flex_hud.global.config.show_background");
	public ConfigInteger backgroundColor = new ConfigInteger(0x313131, "flex_hud.global.config.background_color");

	public AbstractBackgroundModule(@NotNull String id, double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(id, defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);

		ConfigRegistry.register(getID(), "drawBackground", drawBackground);
		ConfigRegistry.register(getID(), "backgroundColor", backgroundColor);
	}

	protected void drawBackground(GuiGraphicsExtractor graphics) {
		drawBackground(0, graphics);
	}

	protected void drawBackground(int index, GuiGraphicsExtractor graphics) {
		DimensionHud dimensionHud = getDimensionHudList().get(index);
		if (drawBackground.getValue() && dimensionHud.isDisplayed() && dimensionHud.getWidth() != 0 && dimensionHud.getHeight() != 0) {
			graphics.fill(-BACKGROUND_PADDING, -BACKGROUND_PADDING, dimensionHud.getWidth() + BACKGROUND_PADDING, dimensionHud.getHeight() + BACKGROUND_PADDING, getBackgroundColor());
		}
	}

	protected int getBackgroundColor() {
		return ARGB.color(0x7f, backgroundColor.getValue());
	}
}
