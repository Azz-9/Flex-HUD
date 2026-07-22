package me.Azz_9.flex_hud.client.gui.components.config.crosshairEditor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import me.Azz_9.flex_hud.client.gui.Colors;
import me.Azz_9.flex_hud.client.gui.Cursors;
import me.Azz_9.flex_hud.client.gui.components.config.DataGetter;
import me.Azz_9.flex_hud.client.gui.components.config.colorSelector.ColorBindable;
import me.Azz_9.flex_hud.utils.DrawingUtils;

public class ColorButton extends AbstractWidget implements ColorBindable, DataGetter<Integer> {
	private int color;
	private final Runnable onPress;

	public ColorButton(int x, int y, int width, int height, Runnable onPress) {
		super(x, y, width, height, Component.empty());
		color = Colors.WHITE;
		this.onPress = onPress;
	}

	@Override
	protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.isHovered()) {
			graphics.requestCursor(Cursors.POINTING_HAND);
			DrawingUtils.drawBorder(graphics, getX(), getY(), getWidth(), getHeight(), 0xffd0d0d0);
		} else {
			DrawingUtils.drawBorder(graphics, getX(), getY(), getWidth(), getHeight(), 0xff404040);
		}

		graphics.fill(getX() + 1, getY() + 1, getRight() - 1, getBottom() - 1, color);
	}

	@Override
	public void onClick(@NotNull MouseButtonEvent event, boolean doubleClick) {
		onPress.run();
	}

	@Override
	public void onReceiveColor(int color) {
		if (this.color != color) {
			this.color = color | 0xff000000;
		}
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public Integer getData() {
		return getColor();
	}

	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
	}
}
