package me.Azz_9.better_hud.client.screens.widgets.buttons.configButtons.colorSelector;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.List;

public class ColorSelecor {
	private GradientWidget gradientWidget;
	//TODO private HueWidget hueWidget;
	//TODO colorField

	private int x;
	private int y;
	private int gap = 2; // gap between elements


	public ColorSelecor(int gradientWidth, int gradientHeight, int hueBarWidth, int hueBarHeight, int hexaFieldWidth, int hexaFieldHeight) {
		this.gradientWidget = new GradientWidget(gradientWidth, gradientHeight);
	}

	public ColorSelecor(int grandientSize, int hueBarWidth, int hueBarHeight, int hexaFieldWidth, int hexaFieldHeight) {
		this(grandientSize, grandientSize, hueBarWidth, hueBarHeight, hexaFieldWidth, hexaFieldHeight);
	}

	public ColorSelecor() {
		this(100, 20, 100, 122, 20);
	}

	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		gradientWidget.setPosition(x + gap, y + gap);

		int width = gradientWidget.getWidth() + gap * 2;
		int height = gradientWidget.getHeight() + gap * 2;

		int backgroundColor = 0xff1e1f22;
		context.fill(x, y, x + width, y + height, backgroundColor);

		gradientWidget.renderWidget(context, mouseX, mouseY, deltaTicks);
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public List<? extends Element> getChildren() {
		return List.of(gradientWidget);
	}
}
