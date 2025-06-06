package me.Azz_9.better_hud.client.screens.widgets.buttons.configButtons.colorSelector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;

public class ColorSelector implements Element, Drawable, ColorUpdatable {
	private GradientWidget gradientWidget;
	private HueWidget hueWidget;
	private ColorFieldWidget colorFieldWidget;

	private int x;
	private int y;
	private int width;
	private int height;
	private final int GAP = 3; // gap between elements
	private final int OUTER_PADDING = 1; // padding between elements and the border of the selector

	private boolean isOpened;


	public ColorSelector(int gradientWidth, int gradientHeight, int hueBarWidth, int hueBarHeight, int hexaFieldWidth, int hexaFieldHeight) {
		this.gradientWidget = new GradientWidget(gradientWidth, gradientHeight, this);
		this.hueWidget = new HueWidget(hueBarWidth, hueBarHeight, this);
		this.colorFieldWidget = new ColorFieldWidget(MinecraftClient.getInstance().textRenderer, hexaFieldWidth, hexaFieldHeight, this);

		this.width = gradientWidth + GAP + hueBarWidth + OUTER_PADDING * 2;
		this.height = gradientHeight + GAP + hexaFieldHeight + OUTER_PADDING * 2;
	}

	public ColorSelector(int grandientSize, int hueBarWidth, int hueBarHeight, int hexaFieldWidth, int hexaFieldHeight) {
		this(grandientSize, grandientSize, hueBarWidth, hueBarHeight, hexaFieldWidth, hexaFieldHeight);
	}

	public ColorSelector() {
		this(100, 16, 100, 100 + 16 + 3, 20);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		int backgroundColor = 0xff1e1f22;
		context.fill(getX(), getY(), getRight(), getBottom(), backgroundColor);

		gradientWidget.renderWidget(context, mouseX, mouseY, deltaTicks);
		hueWidget.renderWidget(context, mouseX, mouseY, deltaTicks);
		colorFieldWidget.renderWidget(context, mouseX, mouseY, deltaTicks);
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		gradientWidget.setPosition(x + OUTER_PADDING, y + OUTER_PADDING);
		hueWidget.setPosition(x + OUTER_PADDING + gradientWidget.getWidth() + GAP, y + OUTER_PADDING);
		colorFieldWidget.setPosition(x + OUTER_PADDING, y + OUTER_PADDING + gradientWidget.getHeight() + GAP);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (gradientWidget.isMouseOver(mouseX, mouseY)) {
			gradientWidget.mouseClicked(mouseX, mouseY, button);
		}
		if (hueWidget.isMouseOver(mouseX, mouseY)) {
			hueWidget.mouseClicked(mouseX, mouseY, button);
		}
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (gradientWidget.isDraggingCursor()) {
			gradientWidget.mouseReleased(mouseX, mouseY, button);
		}
		if (hueWidget.isDraggingCursor()) {
			hueWidget.mouseReleased(mouseX, mouseY, button);
		}
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (gradientWidget.isDraggingCursor()) {
			gradientWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
		if (hueWidget.isDraggingCursor()) {
			hueWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
		return false;
	}

	@Override
	public void onUpdateColor(ColorSelectorElement element) {
		switch (element) {
			case GRADIENT -> {
				//TODO update colorField
			}
			case HUE -> {
				gradientWidget.updateHue(hueWidget.getSelectedHue());
				//TODO update colorField
			}
			case COLOR_FIELD -> {
				//TODO update gradient
				//TODO update huebar
			}
		}
	}

	@Override
	public void setFocused(boolean focused) {
		isOpened = focused;
	}

	@Override
	public boolean isFocused() {
		return isOpened;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= getX() && mouseX <= getRight() && mouseY >= getY() && mouseY <= getBottom();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getRight() {
		return x + width;
	}

	public int getBottom() {
		return y + height;
	}

	public boolean isDraggingACursor() {
		return gradientWidget.isDraggingCursor() || hueWidget.isDraggingCursor();
	}

	enum ColorSelectorElement {
		GRADIENT,
		HUE,
		COLOR_FIELD
	}
}
