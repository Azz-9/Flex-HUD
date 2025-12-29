package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector;

import me.Azz_9.flex_hud.client.utils.Cursors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ColorSelector extends ColorUpdatable implements GuiEventListener, Renderable {
	@NotNull
	private final GradientWidget gradientWidget;
	@NotNull
	private final HueWidget hueWidget;
	@NotNull
	private final ColorFieldWidget colorFieldWidget;
	@NotNull
	private final ColorBindable colorBindable;

	private int x;
	private int y;
	private int width;
	private int height;
	private final int GAP = 3; // gap between elements
	private final int OUTER_PADDING = 1; // padding between elements and the border of the selector

	private boolean isOpened;


	public ColorSelector(int gradientWidth, int gradientHeight, int hueBarWidth, int hueBarHeight, int hexaFieldWidth, int hexaFieldHeight, @NotNull ColorBindable colorBindable) {
		this.colorBindable = colorBindable;

		this.gradientWidget = new GradientWidget(gradientWidth, gradientHeight, this);
		this.hueWidget = new HueWidget(hueBarWidth, hueBarHeight, this);
		this.colorFieldWidget = new ColorFieldWidget(Minecraft.getInstance().font, hexaFieldWidth, hexaFieldHeight, this);

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
	public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.isMouseOver(mouseX, mouseY)) {
			graphics.requestCursor(Cursors.DEFAULT);
		}

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

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		gradientWidget.setPosition(x + OUTER_PADDING, y + OUTER_PADDING);
		hueWidget.setPosition(x + OUTER_PADDING + gradientWidget.getWidth() + GAP, y + OUTER_PADDING);
		colorFieldWidget.setPosition(x + OUTER_PADDING, y + OUTER_PADDING + gradientWidget.getHeight() + GAP);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		if (this.isMouseOver(click.x(), click.y())) {
			for (GuiEventListener child : getChildren()) {
				child.mouseClicked(click, doubled);
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent click) {
		if (this.isMouseOver(click.x(), click.y()) || this.isDraggingACursor()) {
			gradientWidget.mouseReleased(click);
			hueWidget.mouseReleased(click);

			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
		if (this.isMouseOver(click.x(), click.y()) || this.isDraggingACursor()) {
			gradientWidget.mouseDragged(click, offsetX, offsetY);
			hueWidget.mouseDragged(click, offsetX, offsetY);

			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(@NonNull KeyEvent input) {
		if (colorFieldWidget.isFocused()) {
			colorFieldWidget.keyPressed(input);

			return true;
		}
		return false;
	}

	@Override
	public boolean charTyped(@NonNull CharacterEvent input) {
		if (colorFieldWidget.isFocused()) {
			return colorFieldWidget.charTyped(input);
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
