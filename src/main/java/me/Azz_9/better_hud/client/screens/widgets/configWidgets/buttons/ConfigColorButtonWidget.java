package me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.ResetAware;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons.colorSelector.ColorBindable;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class ConfigColorButtonWidget<T> extends ClickableWidget implements TrackableChange, DataGetter<Integer>, ResetAware, ColorBindable {
	private int color;
	private final Consumer<Integer> ON_COlOR_CHANGE;
	private final int INITIAL_COLOR;
	private final List<Observer> observers;
	private final T disableWhen;
	private final Consumer<ConfigColorButtonWidget<T>> onClickAction;
	private final int defaultColor;

	public ConfigColorButtonWidget(int x, int y, int width, int height, int currentColor, int defaultColor, Consumer<Integer> onColorChange, List<Observer> observers, T disableWhen, Consumer<ConfigColorButtonWidget<T>> onClickAction) {
		super(x, y, width, height, Text.empty());
		this.color = currentColor;
		this.INITIAL_COLOR = currentColor;
		this.ON_COlOR_CHANGE = onColorChange;
		this.observers = observers;
		this.disableWhen = disableWhen;
		this.onClickAction = onClickAction;
		this.defaultColor = defaultColor;
	}

	public ConfigColorButtonWidget(int width, int height, int currentColor, int defaultColor, Consumer<Integer> onColorChange, List<Observer> observers, T disableWhen, Consumer<ConfigColorButtonWidget<T>> onClickAction) {
		this(0, 0, width, height, currentColor, defaultColor, onColorChange, observers, disableWhen, onClickAction);
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.active) {
			if (this.isHovered()) {
				Identifier selectedTexture = Identifier.of(MOD_ID, "widgets/buttons/selected.png");
				context.drawTexture(RenderPipelines.GUI_TEXTURED, selectedTexture, this.getX(), this.getY(), 0, 0, this.width, this.height, 100, 20);
			}
			if (this.isSelected()) {
				context.drawBorder(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
			context.drawBorder(getRight() - getHeight(), getY(), getHeight(), getHeight(), (this.isHovered() ? 0xffd0d0d0 : 0xff404040));
		}
		context.fill(getRight() - getHeight() + 1, getY() + 1, getRight() - 1, getBottom() - 1, color | 0xff000000);

		if (!this.active) {
			context.fill(getRight() - getHeight(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		onClickAction.accept(this);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
			onClickAction.accept(this);
			return true;
		}
		return false;
	}

	@Override
	public void setToDefaultState() {
		this.onReceiveColor(defaultColor);
	}

	@Override
	public boolean hasChanged() {
		return this.color != this.INITIAL_COLOR;
	}

	@Override
	public void cancel() {
		ON_COlOR_CHANGE.accept(INITIAL_COLOR);
	}

	@Override
	public Integer getData() {
		return color;
	}

	@Override
	public boolean isSelected() {
		return this.isFocused();
	}

	@Override
	public void onReceiveColor(int color) {
		if (this.color != color) {
			this.color = color;
			ON_COlOR_CHANGE.accept(color);

			for (Observer observer : observers) {
				observer.onChange(this);
			}
		}
	}

	@Override
	public int getColor() {
		return color;
	}

	public T getDisableWhen() {
		return disableWhen;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public boolean isCurrentValueDefault() {
		return color == defaultColor;
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}
}
