package me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.buttons;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.buttons.colorSelector.ColorBindable;
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
	private ConfigInteger variable;
	private final int INITIAL_COLOR;
	private final List<Observer> observers;
	private final T disableWhen;
	private final Consumer<ConfigColorButtonWidget<T>> onClickAction;

	public ConfigColorButtonWidget(int x, int y, int width, int height, ConfigInteger variable, List<Observer> observers, T disableWhen, Consumer<ConfigColorButtonWidget<T>> onClickAction) {
		super(x, y, width, height, Text.empty());
		this.variable = variable;
		this.INITIAL_COLOR = variable.getValue();
		this.observers = observers;
		this.disableWhen = disableWhen;
		this.onClickAction = onClickAction;
	}

	public ConfigColorButtonWidget(int width, int height, ConfigInteger variable, List<Observer> observers, T disableWhen, Consumer<ConfigColorButtonWidget<T>> onClickAction) {
		this(0, 0, width, height, variable, observers, disableWhen, onClickAction);
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
		context.fill(getRight() - getHeight() + 1, getY() + 1, getRight() - 1, getBottom() - 1, variable.getValue() | 0xff000000);

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
		this.onReceiveColor(variable.getDefaultValue());
	}

	@Override
	public boolean hasChanged() {
		return this.variable.getValue() != this.INITIAL_COLOR;
	}

	@Override
	public void cancel() {
		variable.setValue(INITIAL_COLOR);
	}

	@Override
	public Integer getData() {
		return getColor();
	}

	@Override
	public boolean isSelected() {
		return this.isFocused();
	}

	@Override
	public void onReceiveColor(int color) {
		if (this.variable.getValue() != color) {
			this.variable.setValue(color);

			for (Observer observer : observers) {
				observer.onChange(this);
			}
		}
	}

	@Override
	public int getColor() {
		return variable.getValue();
	}

	public T getDisableWhen() {
		return disableWhen;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public boolean isCurrentValueDefault() {
		return variable.getValue().equals(variable.getDefaultValue());
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}
}
