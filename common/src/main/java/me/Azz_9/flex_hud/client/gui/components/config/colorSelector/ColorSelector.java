package me.Azz_9.flex_hud.client.gui.components.config.colorSelector;

import static me.Azz_9.flex_hud.CommonClass.MINECRAFT;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ColorSelector extends ColorUpdatable implements GuiEventListener, Renderable {
	private final @NotNull GradientWidget gradientWidget;
	private final @NotNull HueWidget hueWidget;
	private final @NotNull ColorFieldWidget colorFieldWidget;
	private final @NotNull ColorBindable colorBindable;

	private int x;
	private int y;
	private int width;
	private final int height;
	private final int GAP = 3; // gap between elements
	private final int OUTER_PADDING = 1; // padding between elements and the border of the selector

	private boolean isOpened;


	public ColorSelector(int gradientWidth, int gradientHeight, int hueBarWidth, int hueBarHeight, int hexaFieldWidth, int hexaFieldHeight, @NotNull ColorBindable colorBindable) {
		this.colorBindable = colorBindable;

		this.gradientWidget = new GradientWidget(gradientWidth, gradientHeight, this);
		this.hueWidget = new HueWidget(hueBarWidth, hueBarHeight, this);
		this.colorFieldWidget = new ColorFieldWidget(MINECRAFT.font, hexaFieldWidth, hexaFieldHeight, this);

		this.gradientWidget.updateColor(colorBindable.getColor());
		this.hueWidget.updateHue(colorBindable.getColor());
		this.colorFieldWidget.updateColor(colorBindable.getColor());

		this.width = gradientWidth + GAP + hueBarWidth + OUTER_PADDING * 2;
		this.height = gradientHeight + GAP + hexaFieldHeight + OUTER_PADDING * 2;
	}

	public ColorSelector(int grandientSize, int hueBarWidth, int hueBarHeight, int hexaFieldWidth, int hexaFieldHeight, @NotNull ColorBindable colorBindable) {
		this(grandientSize, grandientSize, hueBarWidth, hueBarHeight, hexaFieldWidth, hexaFieldHeight, colorBindable);
	}

	public ColorSelector(@NotNull ColorBindable colorBindable) {
		this(100, 16, 100, 100 + 16 + 3, 20, colorBindable);
	}

	@Override
	public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		int backgroundColor = 0xff1e1f22;
		graphics.fill(getX(), getY(), getRight(), getBottom(), backgroundColor);

		gradientWidget.render(graphics, mouseX, mouseY, deltaTicks);
		hueWidget.render(graphics, mouseX, mouseY, deltaTicks);
		colorFieldWidget.render(graphics, mouseX, mouseY, deltaTicks);
	}

	public void updatePosition(int scrollableListTop) {
		if (colorBindable.getBottom() > scrollableListTop) {
			setPosition(colorBindable.getRight(), Math.max(colorBindable.getY(), scrollableListTop));
		} else {
			setPosition(colorBindable.getRight(), colorBindable.getBottom());
		}
	}

	public void setWidthGradientGrowth(int width) {
		colorFieldWidget.setWidth(width - OUTER_PADDING * 2);
		gradientWidget.setWidth(width - OUTER_PADDING * 2 - hueWidget.getWidth() - GAP);
		hueWidget.setX(gradientWidget.getRight() + GAP);
		this.width = width;
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
			for (GuiEventListener child : getChildren()) {
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
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (this.isMouseOver(mouseX, mouseY) || this.isDraggingACursor()) {
			gradientWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
			hueWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);

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
	public boolean charTyped(char codePoint, int modifiers) {
		if (colorFieldWidget.isFocused()) {
			return colorFieldWidget.charTyped(codePoint, modifiers);
		}
		return false;
	}

	@Override
	void onUpdateColor(ColorSelectorElement element) {
		if (isIgnoringUpdates()) return;

		runIgnoringUpdates(() -> {
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
		});
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

	private List<GuiEventListener> getChildren() {
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
