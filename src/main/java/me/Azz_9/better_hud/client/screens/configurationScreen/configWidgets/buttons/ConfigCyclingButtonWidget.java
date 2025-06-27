package me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.buttons;

import me.Azz_9.better_hud.client.configurableModules.modules.Translatable;
import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConfigCyclingButtonWidget<T, E extends Enum<E> & Translatable> extends ButtonWidget implements TrackableChange, DataGetter<E>, ResetAware {
	private final Consumer<E> ON_CHANGE;
	private final E INITIAL_STATE;
	private final List<Observer> observers;
	private final T disableWhen;
	private final E[] values;
	private E currentValue;
	private final E defaultValue;
	private Function<E, Tooltip> getTooltip;

	public ConfigCyclingButtonWidget(int width, int height, E currentValue, E defaultValue, Consumer<E> onChange, List<Observer> observers, T disableWhen, @Nullable Function<E, Tooltip> getTooltip) {
		super(0, 0, width, height, Text.translatable(currentValue.getTranslationKey()), (btn) -> {
		}, DEFAULT_NARRATION_SUPPLIER);
		this.ON_CHANGE = onChange;
		this.INITIAL_STATE = currentValue;
		this.observers = observers;
		this.disableWhen = disableWhen;
		this.values = currentValue.getDeclaringClass().getEnumConstants();
		this.currentValue = currentValue;
		this.defaultValue = defaultValue;
		this.getTooltip = getTooltip;

		if (getTooltip != null) this.setTooltip(getTooltip.apply(currentValue));
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.renderWidget(context, mouseX, mouseY, deltaTicks);

		if (!this.active) {
			context.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		super.onClick(mouseX, mouseY);

		int index = (currentValue.ordinal() + 1) % values.length;

		setValue(values[index]);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
			this.onClick(0, 0);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void setToDefaultState() {
		setValue(defaultValue);
	}

	@Override
	public boolean hasChanged() {
		return !currentValue.equals(INITIAL_STATE);
	}

	@Override
	public void cancel() {
		ON_CHANGE.accept(INITIAL_STATE);
	}

	@Override
	public E getData() {
		return currentValue;
	}

	public T getDisableWhen() {
		return disableWhen;
	}

	public void setValue(E value) {
		currentValue = value;
		setMessage(Text.translatable(value.getTranslationKey()));
		if (getTooltip != null) setTooltip(getTooltip.apply(value));
		ON_CHANGE.accept(value);

		for (Observer observer : observers) {
			observer.onChange(this);
		}
	}

	@Override
	public boolean isCurrentValueDefault() {
		return currentValue.equals(defaultValue);
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}
}
