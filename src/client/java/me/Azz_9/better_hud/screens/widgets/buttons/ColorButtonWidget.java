package me.Azz_9.better_hud.screens.widgets.buttons;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import me.Azz_9.better_hud.screens.widgets.colorSelector.ColorEntryWidget;
import me.Azz_9.better_hud.screens.widgets.colorSelector.GradientWidget;
import me.Azz_9.better_hud.screens.widgets.colorSelector.HueBarWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ColorButtonWidget extends ButtonWidget implements TrackableChange {
	private int color;
	private final int INITIAL_COLOR;
	public boolean isSelectingColor = false;
	private int colorSelectorX = getX();
	private int colorSelectorY = getY() + getHeight();
	private final int COLOR_SELECTOR_WIDTH = 120;
	private final int COLOR_SELECTOR_HEIGHT = 124;

	private GradientWidget gradientWidget;
	private HueBarWidget hueBarWidget;
	private ColorEntryWidget colorEntryWidget;

	private final Consumer<Integer> CONSUMER;

	private final int SCREEN_WIDTH;
	private final int SCREEN_HEIGHT;

	public ColorButtonWidget(int width, int height, int currentColor, PressAction onPress, int screenWidth, int screenHeight, Consumer<Integer> consumer) {
		super(0, 0, width, height, Text.of(""), onPress, DEFAULT_NARRATION_SUPPLIER);
		this.color = currentColor;
		this.INITIAL_COLOR = currentColor;

		this.CONSUMER = consumer;

		this.SCREEN_WIDTH = screenWidth;
		this.SCREEN_HEIGHT = screenHeight;

		this.gradientWidget = new GradientWidget(100, 100, this);
		this.hueBarWidget = new HueBarWidget(16, gradientWidget.getHeight(), this, gradientWidget);
		this.colorEntryWidget = new ColorEntryWidget(MinecraftClient.getInstance().textRenderer, COLOR_SELECTOR_WIDTH - 2, 20, this, gradientWidget, hueBarWidget);

		gradientWidget.setColorEntryWidget(colorEntryWidget);
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public int getColorSelectorX() {
		return colorSelectorX;
	}

	public int getColorSelectorY() {
		return colorSelectorY;
	}

	public int getColorSelectorWidth() {
		return COLOR_SELECTOR_WIDTH;
	}

	public int getColorSelectorHeight() {
		return COLOR_SELECTOR_HEIGHT;
	}

	public GradientWidget getGradientWidget() {
		return gradientWidget;
	}

	public HueBarWidget getHueBarWidget() {
		return hueBarWidget;
	}

	public ColorEntryWidget getColorEntryWidget() {
		return colorEntryWidget;
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		if (colorSelectorX + COLOR_SELECTOR_WIDTH > SCREEN_WIDTH) {
			colorSelectorX = colorSelectorX - COLOR_SELECTOR_WIDTH + getWidth();
		} else {
			colorSelectorX = getX();
		}
		gradientWidget.setX(colorSelectorX + 1);
		hueBarWidget.setX(gradientWidget.getRight() + 2);
		colorEntryWidget.setX(gradientWidget.getX());
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		if (colorSelectorY + COLOR_SELECTOR_HEIGHT > SCREEN_HEIGHT) {
			colorSelectorY = colorSelectorY - getHeight() - COLOR_SELECTOR_HEIGHT;
		} else {
			colorSelectorY = getY() + getHeight();
		}
		gradientWidget.setY(colorSelectorY + 1);
		hueBarWidget.setY(gradientWidget.getY());
		colorEntryWidget.setY(gradientWidget.getBottom() + 2);
	}

	@Override
	public void setPosition(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		super.onClick(mouseX, mouseY);
		isSelectingColor = !isSelectingColor;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		CONSUMER.accept(color);

		context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color | 0xFF000000);
		if (this.isFocused()) {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xffd0d0d0);
		} else {
			context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xff404040);
		}
	}

	@Override
	public boolean hasChanged() {
		return color != INITIAL_COLOR;
	}

	@Override
	public void cancel() {
		CONSUMER.accept(INITIAL_COLOR);
	}
}