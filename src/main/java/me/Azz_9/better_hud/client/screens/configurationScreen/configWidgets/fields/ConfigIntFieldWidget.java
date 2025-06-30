package me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.fields;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigIntFieldWidget<T> extends TextFieldWidget implements TrackableChange, DataGetter<Integer>, ResetAware {

	private final int INITIAL_STATE;
	private final ConfigInteger variable;
	private final T disableWhen;
	private final int MIN_VALUE;
	private final int MAX_VALUE;
	private final List<Observer> observers;

	private boolean suppressIntFieldCallback = false;

	public ConfigIntFieldWidget(TextRenderer textRenderer, int width, int height, ConfigInteger variable, List<Observer> observers, T disableWhen) {
		super(textRenderer, width, height, Text.translatable("better_hud.integer_field"));
		this.INITIAL_STATE = variable.getValue();
		this.variable = variable;
		this.disableWhen = disableWhen;
		this.observers = observers;

		if (variable.getMin() < 0)
			throw new IllegalArgumentException("Min value cannot be negative!");
		if (variable.getMax() < 0) throw new IllegalArgumentException("Max value cannot be negative!");
		if (variable.getMin() > variable.getMax())
			throw new IllegalArgumentException("Min value cannot be greater than max value!");

		this.MIN_VALUE = variable.getMin();
		this.MAX_VALUE = variable.getMax();

		String regex = String.format("[0-9]{%d,%d}", String.valueOf(MIN_VALUE).length(), String.valueOf(MAX_VALUE).length());
		setTextPredicate(text -> text.isEmpty() || text.matches(regex));

		setChangedListener(value -> {
			if (suppressIntFieldCallback) return;

			if (value.isEmpty()) {
				value = String.valueOf(MIN_VALUE);
			} else {
				int intValue;
				try {
					intValue = Integer.parseUnsignedInt(value);
				} catch (NumberFormatException e) {
					intValue = Integer.MAX_VALUE;
					value = String.valueOf(intValue);
				}

				if (value.startsWith("0") && value.length() > 1) {
					value = value.substring(1);
				} else if (intValue > MAX_VALUE) {
					value = String.valueOf(MAX_VALUE);
				} else if (intValue < MIN_VALUE) {
					value = String.valueOf(MIN_VALUE);
				}
			}
			setText(value);

			for (Observer observer : observers) {
				observer.onChange(this);
			}
			variable.setValue(getValue());
		});

		setText(String.valueOf(variable.getValue()));
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

	public void increase() {
		if (getValue() < MAX_VALUE) {
			super.setText(String.valueOf(getValue() + 1));
		}
	}

	public void decrease() {
		if (getValue() > MIN_VALUE) {
			super.setText(String.valueOf(getValue() - 1));
		}
	}

	@Override
	public void setText(String text) {
		suppressIntFieldCallback = true;
		super.setText(text);
		suppressIntFieldCallback = false;
	}

	private int getValue() {
		return Integer.parseUnsignedInt(getText());
	}

	public T getDisableWhen() {
		return disableWhen;
	}

	@Override
	public void setToDefaultState() {
		super.setText(String.valueOf(variable.getDefaultValue()));
	}

	@Override
	public boolean hasChanged() {
		return INITIAL_STATE != getValue();
	}

	@Override
	public void cancel() {
		variable.setValue(INITIAL_STATE);
	}

	@Override
	public Integer getData() {
		return getValue();
	}

	@Override
	public boolean isCurrentValueDefault() {
		return getValue() == variable.getDefaultValue();
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}
}
