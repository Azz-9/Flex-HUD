package me.Azz_9.better_hud.client.screens.widgets.configWidgets.slider;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.ResetAware;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class ConfigIntSliderWidget<T> extends SliderWidget implements TrackableChange, DataGetter<Integer>, ResetAware {
	private final Integer STEP;
	private final int MIN_VALUE;
	private final int MAX_VALUE;
	private final int INITIAL_STATE;
	private final Consumer<Integer> ON_CHANGE;
	private final List<Observer> observers;
	private final T disableWhen;
	private final int defaultValue;

	public ConfigIntSliderWidget(int width, int height, int value, int defaultValue, Integer step, int minValue, int maxValue, Consumer<Integer> onChange, List<Observer> observers, T disableWhen) {
		super(0, 0, width, height, Text.of(String.valueOf(value)), (double) (value - minValue) / (maxValue - minValue));
		this.STEP = step;
		this.MIN_VALUE = minValue;
		this.MAX_VALUE = maxValue;
		this.INITIAL_STATE = value;
		this.ON_CHANGE = onChange;
		this.observers = observers;
		this.disableWhen = disableWhen;
		this.defaultValue = defaultValue;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.active) {
			if (this.isSelected()) {
				context.drawBorder(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
		}
		super.renderWidget(context, mouseX, mouseY, deltaTicks);
		if (!this.active) {
			context.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	private double getNormalizedValue(int value) {
		return (double) (value - MIN_VALUE) / (MAX_VALUE - MIN_VALUE);
	}

	private int getRelativeValue() {
		return (int) Math.round(value * (MAX_VALUE - MIN_VALUE) + MIN_VALUE);
	}

	@Override
	public void setToInitialState() {
		value = getNormalizedValue(defaultValue);
		ON_CHANGE.accept(defaultValue);
		updateMessage();

		for (Observer observer : observers) {
			observer.onChange(this);
		}
	}

	@Override
	public boolean hasChanged() {
		return getRelativeValue() != INITIAL_STATE;
	}

	@Override
	public void cancel() {
		ON_CHANGE.accept(INITIAL_STATE);
	}

	@Override
	public Integer getData() {
		return getRelativeValue();
	}

	@Override
	protected void updateMessage() {
		setMessage(Text.of(String.valueOf(getRelativeValue())));
	}

	@Override
	protected void applyValue() {
		if (Screen.hasShiftDown() && STEP != null) {
			// Snap to the nearest multiple of STEP
			int rawValue = getRelativeValue();
			int snappedValue = Math.round((float) rawValue / STEP) * STEP;
			this.value = (snappedValue - MIN_VALUE) / (double) (MAX_VALUE - MIN_VALUE);
		}

		ON_CHANGE.accept(getRelativeValue());

		for (Observer observer : observers) {
			observer.onChange(this);
		}
	}

	public T getDisableWhen() {
		return disableWhen;
	}

	@Override
	public boolean isCurrentValueDefault() {
		return getRelativeValue() == defaultValue;
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}
}
