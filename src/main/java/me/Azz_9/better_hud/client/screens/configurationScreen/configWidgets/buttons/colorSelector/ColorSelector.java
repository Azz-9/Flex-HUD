package me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;

import java.util.List;

public class ColorSelector extends ColorUpdatable implements Element, Drawable {
	private GradientWidget gradientWidget;
	private HueWidget hueWidget;
	private ColorFieldWidget colorFieldWidget;
	private ColorBindable colorBindable;

	private int x;
	private int y;
	private int width;
	private int height;
	private final int GAP = 3; // gap between elements
	private final int OUTER_PADDING = 1; // padding between elements and the border of the selector

	private boolean isOpened;


	public ColorSelector(int gradientWidth, int gradientHeight, int hueBarWidth, int hueBarHeight, int hexaFieldWidth, int hexaFieldHeight, ColorBindable colorBindable) {
		this.gradientWidget = new GradientWidget(gradientWidth, gradientHeight, this);
		this.gradientWidget.updateColor(colorBindable.getColor());
		this.hueWidget = new HueWidget(hueBarWidth, hueBarHeight, this);
		this.hueWidget.updateHue(colorBindable.getColor());
		this.colorFieldWidget = new ColorFieldWidget(MinecraftClient.getInstance().textRenderer, hexaFieldWidth, hexaFieldHeight, this);
		this.colorFieldWidget.updateColor(colorBindable.getColor());

		this.colorBindable = colorBindable;

		this.width = gradientWidth + GAP + hueBarWidth + OUTER_PADDING * 2;
		this.height = gradientHeight + GAP + hexaFieldHeight + OUTER_PADDING * 2;
	}

	public ColorSelector(int grandientSize, int hueBarWidth, int hueBarHeight, int hexaFieldWidth, int hexaFieldHeight, ColorBindable colorBindable) {
		this(grandientSize, grandientSize, hueBarWidth, hueBarHeight, hexaFieldWidth, hexaFieldHeight, colorBindable);
	}

	public ColorSelector(ColorBindable colorBindable) {
		this(100, 16, 100, 100 + 16 + 3, 20, colorBindable);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		int backgroundColor = 0xff1e1f22;
		context.fill(getX(), getY(), getRight(), getBottom(), backgroundColor);

		gradientWidget.renderWidget(context, mouseX, mouseY, deltaTicks);
		hueWidget.renderWidget(context, mouseX, mouseY, deltaTicks);
		colorFieldWidget.renderWidget(context, mouseX, mouseY, deltaTicks);
	}

	public void updatePosition(int scrollableListTop) {
		if (colorBindable.getBottom() > scrollableListTop) {
			setPosition(colorBindable.getRight(), Math.max(colorBindable.getY(), scrollableListTop));
		} else {
			setPosition(colorBindable.getRight(), colorBindable.getBottom());
		}
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
		if (this.isMouseOver(mouseX, mouseY)) {
			for (Element child : getChildren()) {
				child.mouseClicked(mouseX, mouseY, button);
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.isMouseOver(mouseX, mouseY) || this.isDraggingACursor()) {
			gradientWidget.mouseReleased(mouseX, mouseY, button);
			hueWidget.mouseReleased(mouseX, mouseY, button);

			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isMouseOver(mouseX, mouseY) || this.isDraggingACursor()) {
			gradientWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
			hueWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (colorFieldWidget.isFocused()) {
			colorFieldWidget.keyPressed(keyCode, scanCode, modifiers);

			return true;
		}
		return false;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (colorFieldWidget.isFocused()) {
			return colorFieldWidget.charTyped(chr, modifiers);
		}
		return false;
	}

	@Override
	void onUpdateColor(ColorSelectorElement element) {
		int color = 0;
		switch (element) {
			case GRADIENT -> {
				color = gradientWidget.getSelectedColor();
				colorFieldWidget.updateColor(color);
			}
			case HUE -> {
				gradientWidget.updateHue(hueWidget.getSelectedHue());
				color = gradientWidget.getSelectedColor();
				colorFieldWidget.updateColor(color);
			}
			case COLOR_FIELD -> {
				color = colorFieldWidget.getColor();
				gradientWidget.updateColor(color);
				hueWidget.updateHue(color);
			}
		}

		this.colorBindable.onReceiveColor(color);
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

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	private List<Element> getChildren() {
		return List.of(gradientWidget, hueWidget, colorFieldWidget);
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
