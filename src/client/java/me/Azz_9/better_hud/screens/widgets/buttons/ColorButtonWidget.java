package me.Azz_9.better_hud.screens.widgets.buttons;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import me.Azz_9.better_hud.screens.widgets.colorSelector.ColorEntryWidget;
import me.Azz_9.better_hud.screens.widgets.colorSelector.GradientWidget;
import me.Azz_9.better_hud.screens.widgets.colorSelector.HueBarWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class ColorButtonWidget extends ButtonWidget implements TrackableChange {
	private int color;
	private final int INITIAL_COLOR;
	public boolean isSelectingColor = false;
	private int colorSelectorX;
	private int colorSelectorY;
	private final int COLOR_SELECTOR_WIDTH = 120;
	private final int COLOR_SELECTOR_HEIGHT = 124;

	private final GradientWidget GRADIENT_WIDGET;
	private final HueBarWidget HUE_BAR_WIDGET;
	private final ColorEntryWidget COLOR_ENTRY_WIDGET;

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

		this.GRADIENT_WIDGET = new GradientWidget(100, 100, this);
		this.HUE_BAR_WIDGET = new HueBarWidget(16, GRADIENT_WIDGET.getHeight(), this, GRADIENT_WIDGET);
		this.COLOR_ENTRY_WIDGET = new ColorEntryWidget(MinecraftClient.getInstance().textRenderer, COLOR_SELECTOR_WIDTH - 2, 20, this, GRADIENT_WIDGET, HUE_BAR_WIDGET);

		GRADIENT_WIDGET.setColorEntryWidget(COLOR_ENTRY_WIDGET);
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

	public GradientWidget getGRADIENT_WIDGET() {
		return GRADIENT_WIDGET;
	}

	public HueBarWidget getHUE_BAR_WIDGET() {
		return HUE_BAR_WIDGET;
	}

	public ColorEntryWidget getCOLOR_ENTRY_WIDGET() {
		return COLOR_ENTRY_WIDGET;
	}

	public boolean isMouseHoverColorSelector(double mouseX, double mouseY) {
		return mouseX >= colorSelectorX && mouseY >= colorSelectorY && mouseX <= colorSelectorX + COLOR_SELECTOR_WIDTH && mouseY <= colorSelectorY + COLOR_SELECTOR_HEIGHT;
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		colorSelectorX = getRight() - getHeight();

		GRADIENT_WIDGET.setX(colorSelectorX + 1);
		HUE_BAR_WIDGET.setX(GRADIENT_WIDGET.getRight() + 2);
		COLOR_ENTRY_WIDGET.setX(GRADIENT_WIDGET.getX());
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		colorSelectorY = getBottom();

		GRADIENT_WIDGET.setY(colorSelectorY + 1);
		HUE_BAR_WIDGET.setY(GRADIENT_WIDGET.getY());
		COLOR_ENTRY_WIDGET.setY(GRADIENT_WIDGET.getBottom() + 2);
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

		if (this.isFocused() || this.isHovered()) {
			Identifier selectedTexture = Identifier.of(MOD_ID, "widgets/buttons/selected.png");
			context.drawTexture(RenderLayer::getGuiTextured, selectedTexture, this.getX(), this.getY(), 0, 0, getWidth(), getHeight(), 100, 20);

			context.drawBorder(getRight() - getHeight(), getY(), getHeight(), getHeight(), 0xffd0d0d0);
		} else {
			context.drawBorder(getRight() - getHeight(), getY(), getHeight(), getHeight(), 0xff404040);
		}
		context.fill(getRight() - getHeight() + 1, getY() + 1, getRight() - 1, getY() + getHeight() - 1, color | 0xFF000000);
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