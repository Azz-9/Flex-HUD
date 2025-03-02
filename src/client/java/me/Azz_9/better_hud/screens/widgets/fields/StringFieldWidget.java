package me.Azz_9.better_hud.screens.widgets.fields;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class StringFieldWidget extends TextFieldWidget implements TrackableChange {
	private final String initialValue;
	private final Consumer<String> onChange;
	private final Predicate<String> isValid;

	public StringFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, String currentValue, Consumer<String> consumer, Predicate<String> isValid) {
		super(textRenderer, x, y, width, height, Text.of("Text Field"));
		this.initialValue = currentValue;
		this.onChange = consumer;
		this.isValid = isValid;

		setText(currentValue);

		setChangedListener(text -> {
			if (isValid()) {
				consumer.accept(text);
			}
		});
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		super.renderWidget(context, mouseX, mouseY, delta);

		if (!isValid()) {
			if (Formatting.RED.getColorValue() != null) {
				setEditableColor(Formatting.RED.getColorValue());
			} else {
				setEditableColor(0xfc5454);
			}
		} else {
			setEditableColor(0xffffff);
		}
	}

	@Override
	public boolean hasChanged() {
		return !initialValue.equals(getText());
	}

	@Override
	public void cancel() {
		onChange.accept(initialValue);
	}

	@Override
	public boolean isValid() {
		if (isValid == null) {
			return true;
		}
		return isValid.test(getText());
	}
}
