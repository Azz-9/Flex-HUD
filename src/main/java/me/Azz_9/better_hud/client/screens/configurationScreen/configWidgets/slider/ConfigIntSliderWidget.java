package me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.slider;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigIntSliderWidget<T> extends SliderWidget implements TrackableChange, DataGetter<Integer>, ResetAware {
	private final Integer STEP;
	private final int INITIAL_STATE;
	private final ConfigInteger variable;
	private final List<Observer> observers;
	private final T disableWhen;

	public ConfigIntSliderWidget(int width, int height, ConfigInteger variable, Integer step, List<Observer> observers, T disableWhen) {
		super(0, 0, width, height, Text.of(String.valueOf(variable.getValue())), (double) (variable.getValue() - variable.getMin()) / (variable.getMax() - variable.getMin()));
		this.STEP = step;
		this.INITIAL_STATE = variable.getValue();
		this.variable = variable;
		this.observers = observers;
		this.disableWhen = disableWhen;
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
		return (double) (value - variable.getMin()) / (variable.getMax() - variable.getMin());
	}

	private int getRelativeValue() {
		return (int) Math.round(value * (variable.getMax() - variable.getMin()) + variable.getMin());
	}

	@Override
	public void setToDefaultState() {
		value = getNormalizedValue(variable.getDefaultValue());
		variable.setToDefault();
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
		variable.setValue(INITIAL_STATE);
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
			this.value = (snappedValue - variable.getMin()) / (double) (variable.getMax() - variable.getMin());
		}

		variable.setValue(getRelativeValue());

		for (Observer observer : observers) {
			observer.onChange(this);
		}
	}

	public T getDisableWhen() {
		return disableWhen;
	}

	@Override
	public boolean isCurrentValueDefault() {
		return getRelativeValue() == variable.getDefaultValue();
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}
}
