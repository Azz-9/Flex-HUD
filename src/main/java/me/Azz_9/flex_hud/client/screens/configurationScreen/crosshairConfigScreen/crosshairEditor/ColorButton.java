package me.Azz_9.flex_hud.client.screens.configurationScreen.crosshairConfigScreen.crosshairEditor;

import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorBindable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class ColorButton extends ClickableWidget implements ColorBindable, DataGetter<Integer> {
	private int color;
	private Runnable onPress;

	public ColorButton(int x, int y, int width, int height, Runnable onPress) {
		super(x, y, width, height, Text.empty());
		color = 0xffffffff;
		this.onPress = onPress;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.isHovered()) {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xffd0d0d0);
		} else {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xff404040);
		}

		context.fill(getX() + 1, getY() + 1, getRight() - 1, getBottom() - 1, color);
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
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
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}
}
