package me.Azz_9.better_hud.screens.widgets.fields;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class IntFieldWidget extends TextFieldWidget implements TrackableChange {
	private final int INITIAL_VALUE;
	private final Integer MIN_VALUE;
	private final Integer MAX_VALUE;

	public IntFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Integer min, Integer max, int number, Consumer<String> consumer) {
		super(textRenderer, x, y, width, height, Text.translatable("better_hud.integer_field"));
		this.MIN_VALUE = min;
		this.MAX_VALUE = max;

		this.INITIAL_VALUE = number;
		setText(String.valueOf(number));

		setTextPredicate(text -> text.isEmpty() || text.matches(String.format("[0-9]{0,%d}", MAX_VALUE.toString().length())));

		Consumer<String> onChange = value -> {
			if (value.isEmpty()) {
				setText("0");
				return; // setText() method calls this consumer so we need to return to not accept the consumer at the end 2 times
			} else if (value.startsWith("0") && value.length() > 1) {
				setText(value.substring(1));
				return;
			} else if (MAX_VALUE != null && Integer.parseInt(value) > MAX_VALUE) {
				setText(MAX_VALUE.toString());
				return;
			} else if (MIN_VALUE != null && Integer.parseInt(value) < MIN_VALUE) {
				setText(MIN_VALUE.toString());
				return;
			}
			consumer.accept(value);
		};

		setChangedListener(onChange);
	}

	@Override
	public boolean hasChanged() {
		return INITIAL_VALUE != Integer.parseInt(getText());
	}

	@Override
	public void cancel() {
		setText(String.valueOf(INITIAL_VALUE));
	}
}
