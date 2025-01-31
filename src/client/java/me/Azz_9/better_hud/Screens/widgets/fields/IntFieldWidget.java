package me.Azz_9.better_hud.Screens.widgets.fields;

import me.Azz_9.better_hud.client.Interface.TrackableChange;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class IntFieldWidget extends TextFieldWidget implements TrackableChange {
	private int initialValue;
	private Integer minValue;
	private Integer maxValue;

	public IntFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Integer min, Integer max, int number, Consumer<String> consumer) {
		super(textRenderer, x, y, width, height, Text.literal("Integer Field"));
		this.minValue = min;
		this.maxValue = max;

		this.initialValue = number;
		setText(String.valueOf(number));

		setTextPredicate(text -> text.isEmpty() || text.matches("[0-9]{0,2}"));

		Consumer<String> onChange = value -> {
			if (value.isEmpty()) {
				setText("0");
				return;
			} else if (value.startsWith("0") && value.length() > 1) {
				setText(value.substring(1));
				return;
			} else if (maxValue != null && Integer.parseInt(value) > maxValue) {
				setText("14");
				return;
			}
			consumer.accept(value);
		};

		setChangedListener(onChange);
	}

	@Override
	public boolean hasChanged() {
		return initialValue != Integer.parseInt(getText());
	}

	@Override
	public void cancel() {
		setText(String.valueOf(initialValue));
	}
}
