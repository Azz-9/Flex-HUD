package me.Azz_9.flex_hud.client.modules.hud;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.config.ConfigRegistry;
import me.Azz_9.flex_hud.client.config.option.ConfigBoolean;
import me.Azz_9.flex_hud.client.config.option.ConfigInteger;

public abstract class AbstractBackgroundModule extends AbstractMovableModule {
	protected static final int BACKGROUND_PADDING = 2;
	private static final float BACKGROUND_ALPHA = 0.5f;

	public ConfigBoolean drawBackground = new ConfigBoolean(false, "flex_hud.global.config.show_background");
	public ConfigInteger backgroundColor = new ConfigInteger(0x313131, "flex_hud.global.config.background_color");

	public AbstractBackgroundModule(@NotNull String id, double defaultOffsetX, double defaultOffsetY, @NotNull AnchorPosition defaultAnchorX, @NotNull AnchorPosition defaultAnchorY) {
		super(id, defaultOffsetX, defaultOffsetY, defaultAnchorX, defaultAnchorY);

		ConfigRegistry.register(getID(), "drawBackground", drawBackground);
		ConfigRegistry.register(getID(), "backgroundColor", backgroundColor);
	}

	protected void drawBackground(GuiGraphics graphics) {
		drawBackground(0, graphics);
	}

	protected void drawBackground(int index, GuiGraphics graphics) {
		DimensionHud dimensionHud = getDimensionHudList().get(index);
		drawBackground(index, graphics, dimensionHud.getWidth(), dimensionHud.getHeight(), 1);
	}

	public void drawBackground(int index, GuiGraphics graphics, int width, int height, float alphaMultiplier) {
		DimensionHud dimensionHud = getDimensionHudList().get(index);
		if (drawBackground.getValue() && dimensionHud.isDisplayed() && width != 0 && height != 0) {
			graphics.fill(
					-BACKGROUND_PADDING, -BACKGROUND_PADDING,
					width + BACKGROUND_PADDING, height + BACKGROUND_PADDING,
					ARGB.multiplyAlpha(getBackgroundColor(), alphaMultiplier)
			);
		}
	}

	protected int getBackgroundColor() {
		return ARGB.color(BACKGROUND_ALPHA, backgroundColor.getValue());
	}
}
