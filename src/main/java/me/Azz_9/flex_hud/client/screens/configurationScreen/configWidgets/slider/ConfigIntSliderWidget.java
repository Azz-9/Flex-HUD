package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.slider;

import static me.Azz_9.flex_hud.client.Flex_hudClient.MINECRAFT;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Function;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigInteger;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import me.Azz_9.flex_hud.client.utils.Cursors;

public class ConfigIntSliderWidget<T> extends AbstractSliderButton implements TrackableChange, DataGetter<Integer>, ResetAware {
	private final Integer STEP;
	private final int INITIAL_STATE;
	private final ConfigInteger variable;
	private final List<Observer> observers;
	@Nullable
	private final Function<Integer, Tooltip> getTooltip;

	public ConfigIntSliderWidget(int width, int height, ConfigInteger variable, Integer step, List<Observer> observers, @Nullable Function<Integer, Tooltip> getTooltip) {
		super(0, 0, width, height, Component.literal(String.valueOf(variable.getValue())), (double) (variable.getValue() - variable.getMin()) / (variable.getMax() - variable.getMin()));
		this.STEP = step;
		this.INITIAL_STATE = variable.getValue();
		this.variable = variable;
		this.observers = observers;
		this.getTooltip = getTooltip;

		if (this.getTooltip != null) this.setTooltip(this.getTooltip.apply(variable.getValue()));
	}

	@Override
	public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		if (this.active) {
			if (this.isHovered()) graphics.requestCursor(Cursors.POINTING_HAND);

			if (this.isHoveredOrFocused()) {
				graphics.outline(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 0xffffffff);
			}
		}

		super.extractWidgetRenderState(graphics, mouseX, mouseY, deltaTicks);

		if (!this.active) {
			if (this.isHovered()) graphics.requestCursor(Cursors.NOT_ALLOWED);

			graphics.fill(getX(), getY(), getRight(), getBottom(), 0xcf4e4e4e);
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

		if (getTooltip != null) this.setTooltip(this.getTooltip.apply(variable.getValue()));
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
		setMessage(Component.literal(String.valueOf(getRelativeValue())));
	}

	@Override
	protected void applyValue() {
		if (MINECRAFT.hasShiftDown() && STEP != null) {
			// Snap to the nearest multiple of STEP
			int rawValue = getRelativeValue();
			int snappedValue = Math.round((float) rawValue / STEP) * STEP;
			this.value = (snappedValue - variable.getMin()) / (double) (variable.getMax() - variable.getMin());
		}

		variable.setValue(getRelativeValue());

		for (Observer observer : observers) {
			observer.onChange(this);
		}

		if (getTooltip != null) this.setTooltip(this.getTooltip.apply(variable.getValue()));
	}

	@Override
	public boolean isCurrentValueDefault() {
		return getRelativeValue() == variable.getDefaultValue();
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}
}
