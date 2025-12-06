package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.fields;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import me.Azz_9.flex_hud.client.screens.widgets.buttons.TexturedButtonWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class ConfigIntFieldWidget extends TextFieldWidget implements TrackableChange, DataGetter<Integer>, ResetAware {

	private final int INITIAL_STATE;
	private final ConfigInteger variable;
	private final int MIN_VALUE;
	private final int MAX_VALUE;
	private final List<Observer> observers;
	@Nullable
	private final Function<Integer, Tooltip> getTooltip;
	private TexturedButtonWidget increaseButton, decreaseButton;

	private boolean suppressIntFieldCallback = false;

	public ConfigIntFieldWidget(TextRenderer textRenderer, int width, int height, ConfigInteger variable, List<Observer> observers, @Nullable Function<Integer, Tooltip> getTooltip) {
		super(textRenderer, width, height, Text.translatable("flex_hud.integer_field"));
		this.INITIAL_STATE = variable.getValue();
		this.variable = variable;
		this.observers = observers;
		this.getTooltip = getTooltip;

		if (variable.getMin() < 0)
			throw new IllegalArgumentException("Min value cannot be negative!");
		if (variable.getMax() < 0)
			throw new IllegalArgumentException("Max value cannot be negative!");
		if (variable.getMin() > variable.getMax())
			throw new IllegalArgumentException("Min value cannot be greater than max value!");

		this.MIN_VALUE = variable.getMin();
		this.MAX_VALUE = variable.getMax();

		setTextPredicate(text -> text.isEmpty() || text.matches("\\d*") && text.length() <= Integer.toString(MAX_VALUE).length());

		setChangedListener(value -> {
			if (suppressIntFieldCallback) return;

			boolean valid = isValid();

			if (valid) {
				setEditableColor(0xffffffff);
				Integer inputValue = getInputValue();
				if (inputValue != null) { // ça devrait jamais être null ici mais vzy on sait jamais
					variable.setValue(getInputValue());
				}
			} else {
				setEditableColor(ColorHelper.withAlpha(255, Formatting.RED.getColorValue() != null ? Formatting.RED.getColorValue() : 0xfc5454));
			}

			for (Observer observer : observers) {
				observer.onChange(this);
			}

			if (getTooltip != null) this.setTooltip(this.getTooltip.apply(variable.getValue()));

			if (increaseButton != null) increaseButton.active = getValue() < MAX_VALUE;
			if (decreaseButton != null) decreaseButton.active = getValue() > MIN_VALUE;
		});

		setText(String.valueOf(variable.getValue()));

		if (getTooltip != null) {
			this.setTooltip(this.getTooltip.apply(variable.getValue()));
		} else {
			this.setTooltip(Tooltip.of(Text.of("Min: " + MIN_VALUE + (MAX_VALUE == Integer.MAX_VALUE ? "" : "\nMax: " + MAX_VALUE))));
		}
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		if (this.active) {
			if (this.isSelected()) {
				context.drawStrokedRectangle(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
		}
		super.renderWidget(context, mouseX, mouseY, deltaTicks);
		if (!this.active) {
			context.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	public void increase() {
		int current = getValue();
		if (current < MAX_VALUE) {
			super.setText(String.valueOf(current + 1));
		}
	}

	public void decrease() {
		int current = getValue();
		if (current > MIN_VALUE) {
			super.setText(String.valueOf(current - 1));
		}
	}

	public void setIncreaseButton(TexturedButtonWidget increaseButton) {
		this.increaseButton = increaseButton;
		increaseButton.active = getValue() < MAX_VALUE;
	}

	public void setDecreaseButton(TexturedButtonWidget decreaseButton) {
		this.decreaseButton = decreaseButton;
		decreaseButton.active = getValue() > MIN_VALUE;
	}

	@Override
	public void setText(String text) {
		suppressIntFieldCallback = true;
		super.setText(text);
		suppressIntFieldCallback = false;
	}

	private @Nullable Integer getInputValue() {
		try {
			return Integer.parseInt(getText());
		} catch (Exception e) {
			return null;
		}
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
		return variable.getValue();
	}

	private int getValue() {
		return getData();
	}

	@Override
	public boolean isCurrentValueDefault() {
		return getValue() == variable.getDefaultValue();
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public boolean isValid() {
		try {
			Integer number = getInputValue();

			return number != null && number >= MIN_VALUE && number <= MAX_VALUE;
		} catch (Exception e) {
			return false;
		}
	}
}
