package me.Azz_9.better_hud.screens.widgets.fields;

import me.Azz_9.better_hud.client.interfaces.TrackableChange;
import me.Azz_9.better_hud.screens.widgets.buttons.TexturedButtonWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static me.Azz_9.better_hud.client.Better_hudClient.MOD_ID;

public class IntFieldWidget extends TextFieldWidget implements TrackableChange {
	private final int INITIAL_VALUE;
	private final Integer MIN_VALUE;
	private final Integer MAX_VALUE;

	private ButtonWidget increase;
	private ButtonWidget decrease;
	private final int BUTTONS_WIDTH = 10;

	public IntFieldWidget(TextRenderer textRenderer, int width, int height, Integer min, Integer max, int number, Consumer<String> consumer) {
		super(textRenderer, 0, 0, width, height, Text.translatable("better_hud.integer_field"));
		setWidth(width - BUTTONS_WIDTH);
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

		increase = new TexturedButtonWidget(BUTTONS_WIDTH, height / 2, new ButtonTextures(
				Identifier.of(MOD_ID, "widgets/buttons/int_field/increase/unfocused.png"),
				Identifier.of(MOD_ID, "widgets/buttons/int_field/increase/focused.png")
		), (btn) -> setText(String.valueOf(Integer.parseInt(getText()) + 1)), 10, 10);
		decrease = new TexturedButtonWidget(BUTTONS_WIDTH, height / 2, new ButtonTextures(
				Identifier.of(MOD_ID, "widgets/buttons/int_field/decrease/unfocused.png"),
				Identifier.of(MOD_ID, "widgets/buttons/int_field/decrease/focused.png")
		), (btn) -> setText(String.valueOf(Integer.parseInt(getText()) - 1)), 10, 10);
	}

	@Override
	public boolean hasChanged() {
		return INITIAL_VALUE != Integer.parseInt(getText());
	}

	@Override
	public void cancel() {
		setText(String.valueOf(INITIAL_VALUE));
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		increase.setX(x + width);
		decrease.setX(x + width);
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		increase.setY(getY());
		decrease.setY(getY() + height / 2);
	}

	public int getBUTTONS_WIDTH() {
		return BUTTONS_WIDTH;
	}

	public ButtonWidget getIncrease() {
		return increase;
	}

	public ButtonWidget getDecrease() {
		return decrease;
	}
}
