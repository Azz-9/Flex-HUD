package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.fields;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import me.Azz_9.flex_hud.client.screens.widgets.buttons.TexturedButtonWidget;
import me.Azz_9.flex_hud.client.screens.widgets.textFieldWidget.FilteredEditBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ConfigIntFieldWidget extends FilteredEditBox implements TrackableChange, DataGetter<Integer>, ResetAware {

	private final int INITIAL_STATE;
	private final ConfigInteger variable;
	private final int MIN_VALUE;
	private final int MAX_VALUE;
	private final List<Observer> observers;
	@Nullable
	private final Function<Integer, Tooltip> getTooltip;
	private TexturedButtonWidget increaseButton, decreaseButton;

	public ConfigIntFieldWidget(Font font, int width, int height, ConfigInteger variable, List<Observer> observers, @Nullable Function<Integer, Tooltip> getTooltip) {
		super(font, width, height, Component.translatable("flex_hud.integer_field"));
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

		setFilter(text -> text.isEmpty() || text.matches("\\d*") && text.length() <= Integer.toString(MAX_VALUE).length());

		setResponder(value -> {
			boolean valid = isValid();

			if (valid) {
				setTextColor(0xffffffff);
				Integer inputValue = getInputValue();
				if (inputValue != null) { // ça devrait jamais être null ici mais vzy on sait jamais
					variable.setValue(getInputValue());
				}
			} else {
				setTextColor(ARGB.color(255, ChatFormatting.RED.getColor() != null ? ChatFormatting.RED.getColor() : 0xfc5454));
			}

			for (Observer observer : observers) {
				observer.onChange(this);
			}

			if (getTooltip != null) this.setTooltip(this.getTooltip.apply(variable.getValue()));

			if (increaseButton != null) increaseButton.active = getData() < MAX_VALUE;
			if (decreaseButton != null) decreaseButton.active = getData() > MIN_VALUE;
		});

		setValue(String.valueOf(variable.getValue()));

		if (getTooltip != null) {
			this.setTooltip(this.getTooltip.apply(variable.getValue()));
		} else {
			this.setTooltip(Tooltip.create(Component.literal("Min: " + MIN_VALUE + (MAX_VALUE == Integer.MAX_VALUE ? "" : "\nMax: " + MAX_VALUE))));
		}
	}

	@Override
	public void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.active) {
			if (this.isHoveredOrFocused()) {
				graphics.renderOutline(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
		}
		super.renderWidget(graphics, mouseX, mouseY, deltaTicks);
		if (!this.active) {
			graphics.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
		}
	}

	public void increase() {
		int current = getData();
		if (current < MAX_VALUE) {
			super.setValue(String.valueOf(current + 1));
		}
	}

	public void decrease() {
		int current = getData();
		if (current > MIN_VALUE) {
			super.setValue(String.valueOf(current - 1));
		}
	}

	public void setIncreaseButton(TexturedButtonWidget increaseButton) {
		this.increaseButton = increaseButton;
		increaseButton.active = getData() < MAX_VALUE;
	}

	public void setDecreaseButton(TexturedButtonWidget decreaseButton) {
		this.decreaseButton = decreaseButton;
		decreaseButton.active = getData() > MIN_VALUE;
	}

	private @Nullable Integer getInputValue() {
		try {
			return Integer.parseInt(getValue());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void setToDefaultState() {
		super.setValue(String.valueOf(variable.getDefaultValue()));
	}

	@Override
	public boolean hasChanged() {
		return INITIAL_STATE != getData();
	}

	@Override
	public void cancel() {
		variable.setValue(INITIAL_STATE);
	}

	@Override
	public Integer getData() {
		return variable.getValue();
	}

	@Override
	public boolean isCurrentValueDefault() {
		return Objects.equals(getData(), variable.getDefaultValue());
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
