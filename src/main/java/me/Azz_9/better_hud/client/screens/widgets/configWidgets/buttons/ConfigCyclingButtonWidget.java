package me.Azz_9.better_hud.client.screens.widgets.configWidgets.buttons;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import me.Azz_9.better_hud.client.utils.StringUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class ConfigCyclingButtonWidget<T, E extends Enum<E>> extends ButtonWidget implements TrackableChange, DataGetter<E> {
	private final Consumer<E> ON_CHANGE;
	private final E INITIAL_STATE;
	private final List<Observer> observers;
	private final T disableWhen;
	private final E[] values;
	private E currentValue;

	public ConfigCyclingButtonWidget(int width, int height, E currentValue, Consumer<E> onChange, List<Observer> observers, T disableWhen) {
		super(0, 0, width, height, Text.of(StringUtil.capitalize(currentValue.name())), (btn) -> {
		}, DEFAULT_NARRATION_SUPPLIER);
		this.ON_CHANGE = onChange;
		this.INITIAL_STATE = currentValue;
		this.observers = observers;
		this.disableWhen = disableWhen;
		this.values = currentValue.getDeclaringClass().getEnumConstants();
		this.currentValue = currentValue;
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

		currentValue = values[index];
		setMessage(Text.of(StringUtil.capitalize(currentValue.name())));
		ON_CHANGE.accept(currentValue);

		for (Observer observer : observers) {
			observer.onChange(this);
		}
	}

	@Override
	public void setToInitialState() {
		currentValue = INITIAL_STATE;
		setMessage(Text.of(StringUtil.capitalize(INITIAL_STATE.name())));
		ON_CHANGE.accept(currentValue);

		for (Observer observer : observers) {
			observer.onChange(this);
		}
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
}
