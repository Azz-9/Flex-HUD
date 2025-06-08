package me.Azz_9.better_hud.client.screens.widgets.configWidgets.fields;

import me.Azz_9.better_hud.client.screens.TrackableChange;
import me.Azz_9.better_hud.client.screens.configurationScreen.Observer;
import me.Azz_9.better_hud.client.screens.modsList.DataGetter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ConfigIntFieldWidget<T> extends TextFieldWidget implements TrackableChange, DataGetter<Integer> {

	private final Consumer<Integer> onChange;
	private final int INITIAL_STATE;
	private final T disableWhen;

	private final Integer MIN_VALUE;
	private final Integer MAX_VALUE;

	private boolean suppressIntFieldCallback = false;

	public ConfigIntFieldWidget(TextRenderer textRenderer, int width, int height, int currentValue, @Nullable Integer min, @Nullable Integer max, Consumer<Integer> onChange, List<Observer> observers, T disableWhen) {
		super(textRenderer, width, height, Text.translatable("better_hud.integer_field"));
		this.onChange = onChange;
		this.INITIAL_STATE = currentValue;
		this.disableWhen = disableWhen;

		this.MIN_VALUE = min;
		this.MAX_VALUE = max;

		StringBuilder regex = new StringBuilder("[0-9]");
		if (MIN_VALUE != null && MAX_VALUE != null) {
			regex.append("{").append(MIN_VALUE.toString().length()).append(",").append(MAX_VALUE.toString().length()).append("}");
		} else if (MIN_VALUE != null) {
			regex.append("{").append(MIN_VALUE.toString().length()).append(",}");
		} else if (MAX_VALUE != null) {
			regex.append("{,").append(MAX_VALUE.toString().length()).append("}");
		} else {
			regex.append("*");
		}
		setTextPredicate(text -> text.isEmpty() || text.matches(regex.toString()));

		setChangedListener(value -> {
			if (suppressIntFieldCallback) return;

			if (value.isEmpty()) {
				value = "0";
			} else if (value.startsWith("0") && value.length() > 1) {
				value = value.substring(1);
			} else if (MAX_VALUE != null && Integer.parseInt(value) > MAX_VALUE) {
				value = MAX_VALUE.toString();
			} else if (MIN_VALUE != null && Integer.parseInt(value) < MIN_VALUE) {
				value = MIN_VALUE.toString();
			}
			setText(value);

			for (Observer observer : observers) {
				observer.onChange(this);
			}
			onChange.accept(Integer.parseInt(value));
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
		if (MAX_VALUE != null && getValue() < MAX_VALUE) {
			super.setText(String.valueOf(getValue() + 1));
		}
	}

	public void decrease() {
		if (MIN_VALUE != null && getValue() > MIN_VALUE) {
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
		return Integer.parseInt(getText());
	}

	public T getDisableWhen() {
		return disableWhen;
	}

	@Override
	public void setToInitialState() {
		setText(String.valueOf(INITIAL_STATE));
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
}
