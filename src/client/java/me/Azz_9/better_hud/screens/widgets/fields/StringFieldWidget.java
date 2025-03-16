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
	private final String INITIAL_VALUE;
	private final Consumer<String> ON_CHANGE;
	private final Predicate<String> IS_VALID;

	public StringFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, String currentValue, Consumer<String> consumer, Predicate<String> isValid) {
		super(textRenderer, x, y, width, height, Text.translatable("better_hud.text_field"));
		this.INITIAL_VALUE = currentValue;
		this.ON_CHANGE = consumer;
		this.IS_VALID = isValid;

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
		return !INITIAL_VALUE.equals(getText());
	}

	@Override
	public void cancel() {
		ON_CHANGE.accept(INITIAL_VALUE);
	}

	@Override
	public boolean isValid() {
		if (IS_VALID == null) {
			return true;
		}
		return IS_VALID.test(getText());
	}
}
