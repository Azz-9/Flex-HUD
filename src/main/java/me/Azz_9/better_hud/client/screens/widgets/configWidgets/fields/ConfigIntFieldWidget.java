package me.Azz_9.better_hud.client.screens.widgets.configWidgets.fields;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.ResetAware;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ConfigIntFieldWidget<T> extends TextFieldWidget implements TrackableChange, DataGetter<Integer>, ResetAware {

	private final Consumer<Integer> onChange;
	private final int INITIAL_STATE;
	private final T disableWhen;
	private final int defaultValue;

	private final int MIN_VALUE;
	private final int MAX_VALUE;

	private boolean suppressIntFieldCallback = false;

	public ConfigIntFieldWidget(TextRenderer textRenderer, int width, int height, int currentValue, int defaultValue, @Nullable Integer min, @Nullable Integer max, Consumer<Integer> onChange, List<Observer> observers, T disableWhen) {
		super(textRenderer, width, height, Text.translatable("better_hud.integer_field"));
		this.onChange = onChange;
		this.INITIAL_STATE = currentValue;
		this.disableWhen = disableWhen;
		this.defaultValue = defaultValue;

		if (min != null && min < 0) throw new IllegalArgumentException("Min value cannot be negative!");
		if (max != null && max < 0) throw new IllegalArgumentException("Max value cannot be negative!");
		if (min != null && max != null && min > max)
			throw new IllegalArgumentException("Min value cannot be greater than max value!");

		this.MIN_VALUE = (min != null ? min : 0);
		this.MAX_VALUE = (max != null ? max : Integer.MAX_VALUE);

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
			onChange.accept(getValue());
		});

		setText(String.valueOf(currentValue));
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
		super.setText(String.valueOf(defaultValue));
	}

	@Override
	public boolean hasChanged() {
		return INITIAL_STATE != getValue();
	}

	@Override
	public void cancel() {
		onChange.accept(INITIAL_STATE);
	}

	@Override
	public Integer getData() {
		return getValue();
	}

	@Override
	public boolean isCurrentValueDefault() {
		return getValue() == defaultValue;
	}

	public void addObserver(Observer observer) {
		observer.onChange(this);
	}
}
