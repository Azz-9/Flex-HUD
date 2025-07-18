package me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.fields;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.configurationScreen.configVariables.ConfigString;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.DataGetter;
import me.Azz_9.better_hud.client.screens.configurationScreen.configWidgets.ResetAware;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConfigTextFieldWidget<T> extends TextFieldWidget implements TrackableChange, DataGetter<String>, ResetAware {
	private final String INITIAL_VALUE;
	private final ConfigString variable;
	private final Predicate<String> IS_VALID;
	private final List<Observer> observers;
	private final T disableWhen;
	@Nullable
	private final Function<String, Tooltip> getTooltip;

	public ConfigTextFieldWidget(TextRenderer textRenderer, int width, int height, ConfigString variable, List<Observer> observers, T disableWhen, Predicate<String> isValid, @Nullable Function<String, Tooltip> getTooltip) {
		super(textRenderer, width, height, Text.translatable("better_hud.text_field"));
		this.INITIAL_VALUE = variable.getValue();
		this.variable = variable;
		this.IS_VALID = isValid;
		this.observers = observers;
		this.disableWhen = disableWhen;
		this.getTooltip = getTooltip;

		setText(variable.getValue());

		setChangedListener(text -> {
			if (isValid()) {
				variable.setValue(text);
				setEditableColor(0xffffffff);
			} else {
				setEditableColor((Formatting.RED.getColorValue() != null ? Formatting.RED.getColorValue() : 0xfc5454) | 0xff000000);
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
		setText(variable.getValue());
	}

	@Override
	public boolean hasChanged() {
		return !getText().equals(INITIAL_VALUE);
	}

	@Override
	public void cancel() {
		variable.setValue(INITIAL_VALUE);
	}

	@Override
	public String getData() {
		return getText();
	}

	@Override
	public boolean isCurrentValueDefault() {
		return getText().equals(variable.getDefaultValue());
	}

	@Override
	public boolean isValid() {
		if (IS_VALID == null) {
			return true;
		}
		return IS_VALID.test(getText());
	}

	public T getDisableWhen() {
		return disableWhen;
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}
}
