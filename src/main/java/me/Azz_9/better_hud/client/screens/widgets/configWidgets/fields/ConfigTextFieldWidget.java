package me.Azz_9.better_hud.client.screens.widgets.configWidgets.fields;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import me.Azz_9.better_hud.client.screens.widgets.configWidgets.ResetAware;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConfigTextFieldWidget<T> extends TextFieldWidget implements TrackableChange, DataGetter<String>, ResetAware {
	private final String INITIAL_VALUE;
	private final Consumer<String> ON_CHANGE;
	private final Predicate<String> IS_VALID;
	private final String DEFAULT_VALUE;
	private final List<Observer> observers;
	private final T disableWhen;

	public ConfigTextFieldWidget(TextRenderer textRenderer, int width, int height, String currentValue, String defaultValue, Consumer<String> onChange, List<Observer> observers, T disableWhen, Predicate<String> isValid) {
		super(textRenderer, width, height, Text.translatable("better_hud.text_field"));
		this.INITIAL_VALUE = currentValue;
		this.ON_CHANGE = onChange;
		this.IS_VALID = isValid;
		this.DEFAULT_VALUE = defaultValue;
		this.observers = observers;
		this.disableWhen = disableWhen;

		setText(currentValue);

		setChangedListener(text -> {
			if (isValid()) {
				ON_CHANGE.accept(text);
				setEditableColor(0xffffffff);
			} else {
				setEditableColor((Formatting.RED.getColorValue() != null ? Formatting.RED.getColorValue() : 0xfc5454) | 0xff000000);
			}

			for (Observer observer : observers) {
				observer.onChange(this);
			}
		});
	}

	@Override
	public void setToDefaultState() {
		setText(DEFAULT_VALUE);
	}

	@Override
	public boolean hasChanged() {
		return !getText().equals(INITIAL_VALUE);
	}

	@Override
	public void cancel() {
		ON_CHANGE.accept(INITIAL_VALUE);
	}

	@Override
	public String getData() {
		return getText();
	}

	@Override
	public boolean isCurrentValueDefault() {
		return getText().equals(DEFAULT_VALUE);
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
