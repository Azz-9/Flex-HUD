package me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.fields;

import me.Azz_9.flex_hud.client.screens.TrackableChange;
import me.Azz_9.flex_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configVariables.ConfigString;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.flex_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConfigTextFieldWidget extends EditBox implements TrackableChange, DataGetter<String>, ResetAware {
	private final String INITIAL_VALUE;
	private final ConfigString variable;
	private final Predicate<String> IS_VALID;
	private final List<Observer> observers;
	@Nullable
	private final Function<String, Tooltip> getTooltip;

	public ConfigTextFieldWidget(Font font, int width, int height, ConfigString variable, List<Observer> observers, Predicate<String> isValid, @Nullable Function<String, Tooltip> getTooltip) {
		super(font, width, height, Component.translatable("flex_hud.text_field"));
		this.INITIAL_VALUE = variable.getValue();
		this.variable = variable;
		this.IS_VALID = isValid;
		this.observers = observers;
		this.getTooltip = getTooltip;

		setValue(variable.getValue());

		setResponder(text -> {
			if (isValid()) {
				variable.setValue(text);
				setTextColor(0xffffffff);
			} else {
				setTextColor((ChatFormatting.RED.getColor() != null ? ChatFormatting.RED.getColor() : 0xfc5454) | 0xff000000);
			}

			for (Observer observer : observers) {
				observer.onChange(this);
			}

			if (getTooltip != null) this.setTooltip(getTooltip.apply(variable.getValue()));
		});

		if (getTooltip != null) this.setTooltip(getTooltip.apply(variable.getValue()));
	}

	@Override
	public void setToDefaultState() {
		variable.setToDefault();
		setValue(variable.getValue());
	}

	@Override
	public boolean hasChanged() {
		return !getValue().equals(INITIAL_VALUE);
	}

	@Override
	public void cancel() {
		variable.setValue(INITIAL_VALUE);
	}

	@Override
	public String getData() {
		return getValue();
	}

	@Override
	public boolean isCurrentValueDefault() {
		return getValue().equals(variable.getDefaultValue());
	}

	@Override
	public boolean isValid() {
		if (IS_VALID == null) {
			return true;
		}
		return IS_VALID.test(getValue());
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}
}
